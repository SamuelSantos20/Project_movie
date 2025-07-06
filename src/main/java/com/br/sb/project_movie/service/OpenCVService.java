package com.br.sb.project_movie.service;

import com.br.sb.project_movie.config.RabbitConfig;
import com.br.sb.project_movie.dto.AsyncMovieAnalysisMessage;
import com.br.sb.project_movie.model.Movie;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_dnn;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.Net;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.bytedeco.opencv.global.opencv_dnn.readNetFromONNX;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;
import static org.bytedeco.opencv.global.opencv_videoio.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenCVService {

    private static final String YOLO_MODEL_RESOURCE = "model/yolov8n-face-lindevs.onnx";
    private static final String VIDEO_QUEUE = "video-processamento";
    private Net yoloNet;
    private final MovieService movieService;
    private final MovieCacheService movieCacheService;


    private final AnalysisStatusService analysisStatusService;


    static {
        try {
            log.info("Loading OpenCV native libraries...");
            Loader.load(org.bytedeco.opencv.opencv_java.class);
            log.info("OpenCV native libraries loaded successfully.");
        } catch (Exception e) {
            log.error("Failed to load OpenCV native libraries.", e);
            throw new RuntimeException("Native library loading failed.", e);
        }
    }



    @PostConstruct
    @SneakyThrows
    private void init() {
        log.info("Initializing OpenCVService...");
        String yoloPath = new ClassPathResource(YOLO_MODEL_RESOURCE).getFile().getAbsolutePath();
        try {
            yoloNet = readNetFromONNX(yoloPath);
            if (yoloNet.empty()) {
                log.error("YOLO net failed to load from resource: {}. Proceeding without face detection.", YOLO_MODEL_RESOURCE);
                yoloNet = null;
            } else {
                log.info("YOLO net loaded successfully.");
            }
        } catch (Exception e) { // Changed from IOException to Exception
            log.error("Failed to load YOLO net from resource: {}. Proceeding without face detection.", YOLO_MODEL_RESOURCE, e);
            yoloNet = null;
        }
    }

    public boolean detectFacesInImage(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            log.error("Input image bytes are empty or null.");
            throw new IllegalArgumentException("Empty input image.");
        }

        Mat matImage = imdecode(new Mat(new BytePointer(imageBytes)), IMREAD_COLOR);
        if (matImage.empty()) {
            log.error("Failed to decode input image.");
            throw new IOException("Failed to decode input image.");
        }

        boolean facesDetected = detectFacesYOLO(matImage);
        matImage.release();
        log.info("Detected faces in image: {}", facesDetected);
        return facesDetected;
    }

    public boolean detectFacesInVideo(byte[] videoBytes) throws IOException {
        if (videoBytes == null || videoBytes.length == 0) {
            log.error("Input video bytes are empty or null.");
            throw new IllegalArgumentException("Empty input video.");
        }

        File tempInput = File.createTempFile("input", ".mp4");
        try {
            Files.write(tempInput.toPath(), videoBytes);
            VideoCapture capture = new VideoCapture(tempInput.getAbsolutePath());
            if (!capture.isOpened()) {
                log.error("Failed to open video file: {}", tempInput.getAbsolutePath());
                throw new IOException("Cannot open video.");
            }

            double fps = capture.get(CAP_PROP_FPS);
            int width = (int) capture.get(CAP_PROP_FRAME_WIDTH);
            int height = (int) capture.get(CAP_PROP_FRAME_HEIGHT);
            if (fps <= 0 || width <= 0 || height <= 0) {
                log.error("Invalid video properties - FPS: {}, Width: {}, Height: {}", fps, width, height);
                throw new IOException("Invalid video properties.");
            }

            Mat frame = new Mat();
            int maxFrames = 5 * 60 * 30; // 5 minutes at 30 FPS
            int processedFrames = 0;
            boolean faceDetected = false;

            try {
                while (capture.read(frame) && processedFrames < maxFrames && !faceDetected) {
                    if (frame.empty()) {
                        log.warn("Empty frame encountered at frame count: {}", processedFrames + 1);
                        continue;
                    }
                    faceDetected = detectFacesYOLO(frame);
                    processedFrames++;
                }
            } finally {
                frame.release();
                capture.release();
            }
            log.info("Detected faces in video: {}", faceDetected);
            return faceDetected;
        } finally {
            if (!tempInput.delete()) {
                log.warn("Failed to delete temporary input file: {}", tempInput.getAbsolutePath());
            }
        }
    }

    @RabbitListener(queues = RabbitConfig.VIDEO_QUEUE)
    public void processImage(AsyncMovieAnalysisMessage message) {
        log.info("Mensagem recebida: {}", message);
        UUID id = message.getId();
        byte[] imageBytes = message.getImageBytes();

        try {
            log.info("Recebida imagem para análise com ID: {}", id);
            Mat imageMat = imdecode(new Mat(imageBytes), IMREAD_COLOR);
            boolean hasFace = detectFacesYOLO(imageMat);

            if (hasFace) {
                log.info("Rosto detectado com sucesso. Salvando Movie com ID: {}", id);
                analysisStatusService.setStatus(id.toString(), StatusProcessamento.CONCLUIDO.getDescricao());

                // Recuperar o Movie temporário de algum cache/memória e salvar
                Movie movie = movieCacheService.getMovie(message.getId());
                movie.setImage(imageBytes);
                movieService.saveMovie(movie);
                movieCacheService.removeCachedMovie(message.getId());


            } else {
                log.warn("Nenhum rosto detectado. Status marcado como ERRO para ID: {}", id);
                analysisStatusService.setStatus(id.toString(), StatusProcessamento.ERRO.getDescricao());
            }

        } catch (Exception e) {
            log.error("Erro ao processar imagem para ID {}: {}", id, e.getMessage());
            analysisStatusService.setStatus(id.toString(), StatusProcessamento.ERRO.getDescricao());
        }
    }


    public boolean detectFacesYOLO(Mat image) {
        // 1. Verificação inicial da rede
        if (yoloNet == null || yoloNet.empty()) {
            log.error("Rede YOLO não inicializada");
            return false;
        }

        // 2. Parâmetros ajustáveis
        final float CONF_THRESH = 0.5f;
        final float NMS_THRESH = 0.4f;
        final int MIN_FACE_SIZE = 20;

        // 3. Pré-processamento CORRETO com Scalar
        Mat blob = new Mat();
        opencv_dnn.blobFromImage(image, blob, 1.0/255.0,
                new Size(640, 640),
                new Scalar(0.0, 0.0, 0.0, 0.0), // Forma correta
                true, false, opencv_core.CV_32F);
        log.info("Blob shape: {}", blob.size());

        // 4. Passar pela rede
        yoloNet.setInput(blob);
        MatVector outputs = new MatVector();
        yoloNet.forward(outputs, yoloNet.getUnconnectedOutLayersNames());

        // 5. Processar saídas
        Mat detections = outputs.get(0);
        FloatIndexer idx = detections.createIndexer();
        long numDetections = detections.size(2);

        List<Rect> boxes = new ArrayList<>();
        List<Float> confidences = new ArrayList<>();

        for (int i = 0; i < numDetections; i++) {
            log.info("Index: {}", i);
            float confidence = idx.get(0, 4, i);

            if (confidence > CONF_THRESH) {
                float cx = idx.get(0, 0, i);
                float cy = idx.get(0, 1, i);
                float w = idx.get(0, 2, i);
                float h = idx.get(0, 3, i);

            log.info("Confidence: {}", confidence);
            log.info("Center X: {}", cx);
            log.info("Center Y: {}", cy);
            log.info("Width: {}", w);
            log.info("Height: {}", h);

                int left = (int)((cx - w/2) * image.cols());
                int top = (int)((cy - h/2) * image.rows());
                int width = (int)(w * image.cols());
                int height = (int)(h * image.rows());

                if (width >= MIN_FACE_SIZE && height >= MIN_FACE_SIZE) {
                    log.info("Face detected at ({}, {}), size: ({}, {})", left, top, width, height);
                    boxes.add(new Rect(left, top, width, height));
                    confidences.add(confidence);
                }
            }
        }

        // 6. Aplicar NMS
        List<Integer> indices = applyNMS(boxes, confidences, CONF_THRESH, NMS_THRESH);

        // 7. Liberar recursos
        blob.close();
        detections.close();
        outputs.close();

        return !indices.isEmpty();
    }

    private List<Integer> applyNMS(List<Rect> boxes, List<Float> scores,
                                   float scoreThreshold, float nmsThreshold) {
        RectVector boxesVec = new RectVector(boxes.toArray(new Rect[0]));
        FloatPointer scoresPtr = new FloatPointer(scores.size());
        for (int i = 0; i < scores.size(); i++) {
            log.info("Score: {}", scores.get(i));
            scoresPtr.put(i, scores.get(i));
        }

        IntPointer indices = new IntPointer(scores.size());
        opencv_dnn.NMSBoxes(boxesVec, scoresPtr, scoreThreshold, nmsThreshold, indices);

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < indices.limit(); i++) {
            log.info("Index: {}", indices.get(i));
            result.add(indices.get(i));
        }

        return result;
    }

    private boolean isValidFace(Mat faceRegion) {
        try {
            Mat hsv = new Mat();
            opencv_imgproc.cvtColor(faceRegion, hsv, opencv_imgproc.COLOR_BGR2HSV);

            // Forma CORRETA de criar Scalar para inRange
            Mat lowerBound = new Mat(1, 1, opencv_core.CV_8UC3, new Scalar(0.0, 30.0, 60.0, 0.0));
            Mat upperBound = new Mat(1, 1, opencv_core.CV_8UC3, new Scalar(25.0, 150.0, 255.0, 0.0));

            Mat skinMask = new Mat();
            opencv_core.inRange(hsv, lowerBound, upperBound, skinMask);

            double skinPixels = opencv_core.countNonZero(skinMask);
            double skinRatio = skinPixels / (faceRegion.rows() * faceRegion.cols());

            return skinRatio > 0.2;
        } catch (Exception e) {
            log.error("Erro na verificação facial", e);
            return false;
        }
    }

    private List<Integer> nonMaximumSuppression(List<Rect> boxes, List<Float> confidences, float confThreshold, float nmsThreshold) {
        List<Integer> indices = new ArrayList<>();
        List<Integer> order = IntStream.range(0, boxes.size())
                .boxed()
                .sorted((i, j) -> Float.compare(confidences.get(j), confidences.get(i)))
                .collect(Collectors.toList());

        boolean[] suppressed = new boolean[boxes.size()];
        for (int i = 0; i < order.size(); i++) {
            int idx = order.get(i);
            if (suppressed[idx] || confidences.get(idx) < confThreshold) {
                continue;
            }
            indices.add(idx);
            Rect rect1 = boxes.get(idx);
            for (int j = i + 1; j < order.size(); j++) {
                int idx2 = order.get(j);
                if (suppressed[idx2]) {
                    continue;
                }
                Rect rect2 = boxes.get(idx2);
                float iou = computeIoU(rect1, rect2);
                if (iou > nmsThreshold) {
                    suppressed[idx2] = true;
                }
            }
        }
        return indices;
    }

    private float computeIoU(Rect a, Rect b) {
        int x1 = Math.max(a.x(), b.x());
        int y1 = Math.max(a.y(), b.y());
        int x2 = Math.min(a.x() + a.width(), b.x() + b.width());
        int y2 = Math.min(a.y() + a.height(), b.y() + b.height());

        int interArea = Math.max(0, x2 - x1 + 1) * Math.max(0, y2 - y1 + 1);
        int boxAArea = a.width() * a.height();
        int boxBArea = b.width() * b.height();
        return (float) interArea / (boxAArea + boxBArea - interArea);
    }
}