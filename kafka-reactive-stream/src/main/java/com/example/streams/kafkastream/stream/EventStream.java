package com.example.streams.kafkastream.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;


@Configuration
@Slf4j
public class EventStream {


    private static final byte[] KEY_NOT_FOUND = "notFound".getBytes(StandardCharsets.UTF_8);

    @Bean
    public Consumer<Flux<Message<String>>> eventConsumer() {
        return flux -> flux
            .map(msg -> {
                var payload = msg.getPayload();
                String key = new String((byte[]) msg.getHeaders().getOrDefault("kafka_receivedMessageKey", KEY_NOT_FOUND), StandardCharsets.UTF_8);
                log.info("EVENTOO CONSUMIDO--> ID: {}, Key: {}, Value: {}", msg.getHeaders().getId(), key, msg.getPayload());
                return msg;
            })
            .subscribe();
    }

    @Bean
    public Function<Flux<Message<String>>, Flux<Message<String>>> eventProcess() {
        return eventsInputs -> eventsInputs
            .map(Message::getPayload)
            .map(b -> b.toUpperCase(Locale.ROOT))
            .doOnNext(evt -> log.info("EVENTOO PROCESADO--> {}", evt))
            .map(x -> MessageBuilder.withPayload(x).build());
    }
}
