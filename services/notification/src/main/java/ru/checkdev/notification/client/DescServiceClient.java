package ru.checkdev.notification.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.dto.CategoryDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class DescServiceClient {
    private final WebClient webClient;

    /**
     * Метод get
     *
     * @param url URL http
     * @return Mono<Person>
     */
    public Mono<Object> doGet(String url) {
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    public Flux<CategoryDto> doGetAll(String url) {
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(CategoryDto.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    /**
     * Метод POST
     *
     * @param url       URL http
     * @param requestBody Body PersonDTO.class
     * @return Mono<Person>
     */
    public Mono<Object> doPost(String url, Object requestBody) {
        return webClient
                .post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

}
