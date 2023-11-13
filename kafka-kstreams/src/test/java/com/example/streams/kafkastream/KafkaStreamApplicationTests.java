package com.example.streams.kafkastream;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Profile("test")
@EmbeddedKafka(
    partitions = 1,
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    topics = {"my-topic", "my-topic-stream"}
)
@DirtiesContext
class KafkaStreamApplicationTests {

    @Autowired
    private KafkaTemplate<String, String> template;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void testSendReceive() {

        this.template.send("my-topic", "key1", "value1");

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("group-eventProcess", "false", embeddedKafkaBroker);
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);

        Consumer<String, String> consumer = cf.createConsumer();
        consumer.assign(Collections.singleton(new TopicPartition("my-topic-stream", 0)));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        consumer.commitSync();

        assertThat(records.count()).isEqualTo(1);
        records.records("my-topic-stream").forEach(event -> {
            assertThat(event.value()).asString().isEqualTo("VALUE1");
        });

    }

}
