package com.iot.simulator.service.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.iot.shared.event.ReadingCreatedEvent;
import com.iot.simulator.adapter.KafkaEventProducerAdapter;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

@EmbeddedKafka(topics = {KafkaEventProducerAdapterTest.IOT_READINGS_TOPIC})
@SpringBootTest(
    classes = {
      KafkaEventProducerAdapter.class,
      KafkaEventProducerAdapterTest.KafkaTestConfig.class
    })
@TestPropertySource("classpath:application-test.properties")
public class KafkaEventProducerAdapterTest {
  public static final String IOT_READINGS_TOPIC = "iot-reading-topic";
  @Autowired private KafkaEventProducerAdapter producerAdapter;

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Test
  void testSend_eventIsPublished() {
    ReadingCreatedEvent event =
        new ReadingCreatedEvent(
            "sensor1", "temperature", java.util.List.of("group1"), Instant.now(), 25.5);

    var consumerFactory = configureKafkaConsumerFactory();

    try (var consumer = consumerFactory.createConsumer()) {
      embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, IOT_READINGS_TOPIC);

      producerAdapter.send(event);

      ConsumerRecord<String, ReadingCreatedEvent> received =
          KafkaTestUtils.getSingleRecord(consumer, IOT_READINGS_TOPIC, Duration.ofSeconds(2));

      assertThat(received).isNotNull();
      assertThat(received.key()).isEqualTo("sensor1");
      assertThat(received.value()).isEqualTo(event);
    }
  }

  private DefaultKafkaConsumerFactory<String, ReadingCreatedEvent> configureKafkaConsumerFactory() {
    Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
    consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

    return new DefaultKafkaConsumerFactory<>(
        consumerProps,
        new StringDeserializer(),
        new JsonDeserializer<>(ReadingCreatedEvent.class, false));
  }

  @TestConfiguration
  static class KafkaTestConfig {
    @Bean
    public KafkaTemplate<String, ReadingCreatedEvent> kafkaTemplate(
        EmbeddedKafkaBroker embeddedKafkaBroker) {
      var producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
      var producerFactory =
          new DefaultKafkaProducerFactory<>(
              producerProps, new StringSerializer(), new JsonSerializer<ReadingCreatedEvent>());
      return new KafkaTemplate<>(producerFactory);
    }
  }
}
