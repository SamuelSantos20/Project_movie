package com.br.sb.project_movie.service;

import com.br.sb.project_movie.config.RabbitConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.javacpp.BytePointer;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OpenCVRabbitListener {

    @Autowired
    private OpenCVService openCVService; // seu serviço com detectFacesYOLO(Mat)

    @RabbitListener(queues = RabbitConfig.VIDEO_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void receiveImageFromQueue(byte[] imageBytes, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            log.info("Recebido imagem da fila, tamanho bytes: {}", imageBytes.length);

            // Converter bytes para Mat OpenCV
            Mat imgMat = opencv_imgcodecs.imdecode(new Mat(new BytePointer(imageBytes)), opencv_imgcodecs.IMREAD_COLOR);

            if (imgMat == null || imgMat.empty()) {
                log.warn("Imagem inválida ao decodificar");
                channel.basicNack(tag, false, false); // rejeita sem requeue
                return;
            }

            // Detectar faces usando seu método
            boolean facesDetected = openCVService.detectFacesYOLO(imgMat);
            log.info("Faces detectadas? {}", facesDetected);

            // Aqui você pode salvar resultado no banco, por exemplo

            channel.basicAck(tag, false); // confirma processamento da mensagem

        } catch (Exception e) {
            log.error("Erro ao processar mensagem da fila", e);
            try {
                channel.basicNack(tag, false, true); // reenvia a mensagem para fila para tentar novamente
            } catch (IOException ex) {
                log.error("Erro ao enviar NACK para fila", ex);
                throw new RuntimeException(ex);
            }
        }
    }
}
