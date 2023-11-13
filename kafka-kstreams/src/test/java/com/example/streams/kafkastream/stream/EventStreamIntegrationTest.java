package com.example.streams.kafkastream.stream;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Iterator;

import static com.example.streams.kafkastream.stream.EventStreamIntegrationTest.TEST_TOPIC_IN;
import static com.example.streams.kafkastream.stream.EventStreamIntegrationTest.TEST_TOPIC_OUT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Profile("test")
@EmbeddedKafka(
//    controlledShutdown = true,
//    partitions = 1,
//    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    topics = {TEST_TOPIC_IN, TEST_TOPIC_OUT}
)
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventStreamIntegrationTest {

    static final String TEST_TOPIC_IN = "my-topic";
    static final String TEST_TOPIC_OUT = "my-topic-stream";

    @Autowired
    private KafkaTemplate<String, String> template;
    @Autowired
    private DefaultKafkaConsumerFactory<String, String> consumerFactory;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private Consumer<String, String> consumer;


    @BeforeAll
    void setup() {
        consumer = this.consumerFactory.createConsumer("group-id-test", "");
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, TEST_TOPIC_OUT);
    }


    @Test
    void should_produce_once_event() {

        this.template.send(TEST_TOPIC_IN, "key", "test");

        var message = KafkaTestUtils.getSingleRecord(consumer, TEST_TOPIC_OUT);

        assertThat(message.value()).isEqualTo("TEST");
    }


    @Test
    void should_produce_multiple_event() {

        this.template.send(TEST_TOPIC_IN, "key", "test");
        this.template.send(TEST_TOPIC_IN, "key", "test2");

        var replies = KafkaTestUtils.getRecords(consumer);

        assertThat(replies.count()).isEqualTo(2);

        Iterator<ConsumerRecord<String, String>> iterator = replies.iterator();
        assertThat(iterator.next().value()).isEqualTo("TEST");
        assertThat(iterator.next().value()).isEqualTo("TEST2");
    }


}