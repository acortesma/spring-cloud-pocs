package com.example.streams.kafkastream.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
@Slf4j
public class EventStream {

    @Bean
    public Consumer<KStream<String, String>> eventConsumer() {

        return input ->
            input.foreach((key, value) -> {
                log.info("EVENTOO CONSUMIDO--> Key: {}, Value: {}", key, value);
            });
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, String>> eventProcess() {

        return input -> input
            .map((key, value) -> new KeyValue<>(key, value.toUpperCase(Locale.ROOT)))
            .peek((key, value) -> log.info("EVENTOO PROCESADO--> {}", key + " " + value));
    }
}
