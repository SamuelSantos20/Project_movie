package com.br.sb.project_movie.config;

import com.br.sb.project_movie.dto.AsyncMovieAnalysisMessage;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    // Fila para processamento de vídeo
    public static final String VIDEO_QUEUE = "video-processamento";

    // Configurações para análise de imagem com rosto
    public static final String ANALYSIS_QUEUE = "movie.analysis.queue";
    public static final String ANALYSIS_EXCHANGE = "movie.analysis.exchange";
    public static final String ANALYSIS_ROUTING_KEY = "movie.analysis.routingkey";

    // Conversor de mensagens para JSON
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.br.sb.project_movie.dto.AsyncMovieAnalysisMessage", AsyncMovieAnalysisMessage.class);
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);
        return converter;
    }


    // Configuração do listener com ACK manual
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // ou AUTO se preferir
        return factory;
    }

    // Fila do vídeo (já existente)
    @Bean
    public Queue videoQueue() {
        return new Queue(VIDEO_QUEUE, true);
    }

    // Fila para análise facial
    @Bean
    public Queue analysisQueue() {
        return new Queue(ANALYSIS_QUEUE, true);
    }

    // Exchange para análise
    @Bean
    public DirectExchange analysisExchange() {
        return new DirectExchange(ANALYSIS_EXCHANGE);
    }

    // Binding da fila com a exchange e routing key
    @Bean
    public Binding analysisBinding() {
        return BindingBuilder
                .bind(analysisQueue())
                .to(analysisExchange())
                .with(ANALYSIS_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

}
