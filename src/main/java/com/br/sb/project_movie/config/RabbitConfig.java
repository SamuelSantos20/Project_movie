package com.br.sb.project_movie.config;

import com.br.sb.project_movie.dto.AsyncMovieAnalysisMessage;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    public static final String VIDEO_QUEUE = "video-processamento";
    public static final String ANALYSIS_QUEUE = "movie.analysis.queue";
    public static final String ANALYSIS_EXCHANGE = "movie.analysis.exchange";
    public static final String ANALYSIS_ROUTING_KEY = "movie.analysis.routingkey";

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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setAutoStartup(false);
        return factory;
    }

    @Bean
    public Queue videoQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "video-processamento.dlx");
        args.put("x-dead-letter-routing-key", "video-processamento.dlq");
        return new Queue(VIDEO_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue analysisQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "movie.analysis.dlx");
        args.put("x-dead-letter-routing-key", "movie.analysis.dlq");
        return new Queue(ANALYSIS_QUEUE, true, false, false, args);
    }

    @Bean
    public DirectExchange analysisExchange() {
        return new DirectExchange(ANALYSIS_EXCHANGE);
    }

    @Bean
    public Binding analysisBinding() {
        return BindingBuilder.bind(analysisQueue()).to(analysisExchange()).with(ANALYSIS_ROUTING_KEY);
    }

    @Bean
    public DirectExchange videoDeadLetterExchange() {
        return new DirectExchange("video-processamento.dlx");
    }

    @Bean
    public Queue videoDeadLetterQueue() {
        return new Queue("video-processamento.dlq", true);
    }

    @Bean
    public Binding videoDeadLetterBinding() {
        return BindingBuilder.bind(videoDeadLetterQueue()).to(videoDeadLetterExchange()).with("video-processamento.dlq");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("movie.analysis.dlx");
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("movie.analysis.dlq", true);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("movie.analysis.dlq");
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

    @Bean
    public ApplicationRunner clearQueueOnStartup(RabbitAdmin rabbitAdmin, SimpleRabbitListenerContainerFactory factory) {
        return args -> {
            // Deletar filas apenas se existirem
            String[] queues = {VIDEO_QUEUE, ANALYSIS_QUEUE, "video-processamento.dlq", "movie.analysis.dlq"};
            for (String queue : queues) {
                QueueInformation queueInfo = rabbitAdmin.getQueueInfo(queue);
                if (queueInfo != null) {
                    rabbitAdmin.deleteQueue(queue);
                    System.out.println("Fila '" + queue + "' deletada com sucesso.");
                } else {
                    System.out.println("Fila '" + queue + "' não existe, pulando deleção.");
                }
            }

            // Recriar filas
            rabbitAdmin.initialize();
            System.out.println("Filas recriadas com sucesso.");

            // Verificar mensagens restantes
            for (String queue : queues) {
                QueueInformation queueInfo = rabbitAdmin.getQueueInfo(queue);
                System.out.println("Mensagens restantes na fila '" + queue + "': " +
                        (queueInfo != null ? queueInfo.getMessageCount() : "Fila não existe"));
            }

            // Iniciar consumidores
            SimpleMessageListenerContainer container = factory.createListenerContainer();
            container.start();
            System.out.println("Consumidores iniciados.");
        };
    }
}