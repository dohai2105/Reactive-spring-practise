package com.example.reservationservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@SpringBootApplication
public class ReservationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest {
    private String name;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GrettingResponse {
    private String greeting;
}

@Component
class IntervalMessageProducer {
    Flux<GrettingResponse> producerGreetings(GreetingRequest name) {
        return Flux.fromStream((Stream.generate(() -> "Hello" + name.getName() + "@" + Instant.now())))
                .map(GrettingResponse::new)
                .delayElements(Duration.ofSeconds(1));
    }
}

@RestController
@RequiredArgsConstructor
class ReservationRestController {

    @Autowired
    private IntervalMessageProducer imp;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/test/{n}")
    Publisher<GrettingResponse> stringPublisher(@PathVariable String n) {
        return imp.producerGreetings(new GreetingRequest(n));
    }
}
