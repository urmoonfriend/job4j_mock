package ru.checkdev.notification.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.client.DescServiceClient;
import ru.checkdev.notification.client.TgAuthCallWebClint;
import ru.checkdev.notification.domain.PersonDTO;

import java.util.Calendar;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Testing TgAuthCallWebClint
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 06.10.2023
 */
@SpringBootTest
//@ExtendWith(MockitoExtension.class)
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
        int personId = 100;
        var created = new Calendar.Builder()
                .set(Calendar.DAY_OF_MONTH, 23)
                .set(Calendar.MONTH, Calendar.OCTOBER)
                .set(Calendar.YEAR, 2023)
                .build();
        var personDto = new PersonDTO().setEmail("email@gmail.com")
                .setPassword("password")
                .setPrivacy(true)
                .setRoles(null)
                .setCreated(Calendar.getInstance());

        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(anyString())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(PersonDTO.class)).thenReturn(Mono.just(personDto));

        PersonDTO actual = (PersonDTO) descServiceClient.doGet("/person/" + personId).block();
        assertThat(actual).isEqualTo(personDto);
    }

    @Test
    void whenDoGetThenReturnExceptionError() {
        int personId = 100;
        /*
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri("/person/" + personId)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(PersonDTO.class)).thenReturn(Mono.error(new Throwable("Error")));
         */
        assertThatThrownBy(() -> descServiceClient.doGet("/person/" + personId).block())
                .isInstanceOf(Throwable.class)
                .hasMessageContaining("Error");
    }

    @Test
    void whenDoPostSavePersonThenReturnNewPerson() {
        var created = new Calendar.Builder()
                .set(Calendar.DAY_OF_MONTH, 23)
                .set(Calendar.MONTH, Calendar.OCTOBER)
                .set(Calendar.YEAR, 2023)
                .build();
        var personDto = new PersonDTO().setEmail("email@gmail.com")
                .setPassword("password")
                .setPrivacy(true)
                .setRoles(null)
                .setCreated(Calendar.getInstance());
        /*
        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri("/person/created")).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(personDto)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(Object.class)).thenReturn(Mono.just(personDto));
         */
        Mono<Object> objectMono = descServiceClient.doPost("/person/created", personDto);
        PersonDTO actual = (PersonDTO) objectMono.block();
        assertThat(actual).isEqualTo(personDto);
    }
}