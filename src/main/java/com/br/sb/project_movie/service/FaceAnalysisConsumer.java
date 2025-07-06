package com.br.sb.project_movie.service;

import com.br.sb.project_movie.config.RabbitConfig;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor

public class FaceAnalysisConsumer {

    private final OpenCVService openCVService;
    private final AnalysisStatusService statusService;

    @RabbitListener(queues = RabbitConfig.ANALYSIS_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        try {
            String movieId = (String) message.get("movieId");
            byte[] imageBytes = (byte[]) message.get("imageBytes");

            statusService.setStatus(movieId, "Análise em andamento");

            boolean hasFaces = openCVService.detectFacesInImage(imageBytes);

            if (hasFaces) {
                statusService.setStatus(movieId, "Faces detectadas");
            } else {
                statusService.setStatus(movieId, "Nenhuma face detectada");
            }

            channel.basicAck(tag, false);

        } catch (Exception e) {
            channel.basicNack(tag, false, false);
        }
    }
}


