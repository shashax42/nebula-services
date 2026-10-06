package io.nebula.market.product.infrastructure.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import io.nebula.market.product.domain.event.OrderCanceledEvent;
import io.nebula.market.product.domain.event.OrderPlacedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.template.default-topic}")
    private String defaultTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, OrderPlacedEvent> consumerFactory() {
        Map<String, Object> props = Map.ofEntries(
                Map.entry(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
                Map.entry(ConsumerConfig.GROUP_ID_CONFIG, groupId),
                Map.entry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class),
                Map.entry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class)
        );

        var deserializer = new JsonDeserializer<>(OrderPlacedEvent.class, false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent>();
        factory.setConsumerFactory(consumerFactory());
        // 커스텀 팩토리는 spring.kafka.listener.* 자동설정을 받지 않으므로 직접 켠다
        // → 수신 측 span 이 생성되고 producer 의 trace context 를 이어받는다 (사가 전체가 한 트레이스)
        factory.getContainerProperties().setObservationEnabled(true);
        return factory;
    }

    @Bean
    public ProducerFactory<String, OrderCanceledEvent> producerFactory() {
        Map<String, Object> props = Map.ofEntries(
                Map.entry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
                Map.entry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class),
                Map.entry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
        );

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, OrderCanceledEvent> kafkaTemplate() {
        var kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(defaultTopic);
        // 커스텀 KafkaTemplate 은 spring.kafka.template.observation-enabled 가 적용되지 않으므로 직접 켠다
        // → 메시지 헤더에 traceparent 가 실려 소비자와 같은 트레이스로 연결된다
        kafkaTemplate.setObservationEnabled(true);
        return kafkaTemplate;
    }

}
