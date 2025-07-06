package com.br.sb.project_movie.service;

import com.br.sb.project_movie.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FaceAnalysisProducer {

    private final RabbitTemplate rabbitTemplate;

    // Envia dados da imagem para a fila de análise
    public void sendImageForAnalysis(String movieId, byte[] imageBytes) {
        // Você pode enviar um objeto mais complexo aqui se quiser (exemplo usando Map)
        Map<String, Object> message = Map.of(
                "movieId", movieId,
                "imageBytes", imageBytes
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.ANALYSIS_EXCHANGE,
                RabbitConfig.ANALYSIS_ROUTING_KEY,
                message
        );
    }
}
