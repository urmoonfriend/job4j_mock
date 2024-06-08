package ru.checkdev.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.client.DescServiceClient;
import ru.checkdev.notification.domain.PersonDTO;

import java.util.Calendar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Testing TgAuthCallWebClint
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 06.10.2023
 */
@SpringBootTest
@Slf4j
class DescServiceClientTest {
    private static final String URL = "http://tetsurl:15001";
    @MockBean
    private WebClient webClientMock;
    @MockBean
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @MockBean
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @MockBean
    private WebClient.RequestBodySpec requestBodyMock;
    @MockBean
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    @MockBean
    private WebClient.ResponseSpec responseMock;
    @Autowired
    private DescServiceClient descServiceClient;

    @Test
    void whenDoGetThenReturnPersonDTO() {
        var personDto = new PersonDTO().setEmail("email@gmail.com")
                .setPassword("password")
                .setPrivacy(true)
                .setRoles(null)
                .setCreated(Calendar.getInstance());

        // Mock the WebClient's behavior
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(anyString())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(PersonDTO.class)).thenReturn(Mono.just(personDto));

        // Use a specific URL string for the doGet method
        String testUrl = "http://test.url";
        PersonDTO actual = descServiceClient.doGet(testUrl, PersonDTO.class).block();

        log.info("actual: {}", actual);
        assertThat(actual).isEqualTo(personDto);
        // assertThat(personDto.getEmail()).isEqualTo(actual.getEmail());
    }


    @Test
    void whenDoGetThenReturnExceptionError() {
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(anyString())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(PersonDTO.class)).thenReturn(Mono.error(new Throwable("Error")));
        assertThatThrownBy(() -> descServiceClient.doGet(anyString(), PersonDTO.class).block())
                .isInstanceOf(Throwable.class)
                .hasMessageContaining("Error");
    }

    @Test
    void whenDoPostSavePersonThenReturnNewPerson() {
        var personDto = new PersonDTO().setEmail("email@gmail.com")
                .setPassword("password")
                .setPrivacy(true)
                .setRoles(null)
                .setCreated(Calendar.getInstance());
        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri("/person/created")).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(personDto)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(Object.class)).thenReturn(Mono.just(personDto));
        Mono<Object> objectMono = descServiceClient.doPost("/person/created", personDto);
        PersonDTO actual = (PersonDTO) objectMono.block();
        assertThat(actual).isEqualTo(personDto);
    }
}