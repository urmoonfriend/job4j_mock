package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.FilterDTO;

@Service
@RequiredArgsConstructor
public class FilterService {
    private static final String URL = "http://localhost:9912/filter/";
    private final RestAuthCall restAuthCall;

    public FilterDTO save(String token, FilterDTO filter) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var out = restAuthCall.setUrl(URL).post(
                token,
                mapper.writeValueAsString(filter)
        );
        return mapper.readValue(out, FilterDTO.class);
    }

    public FilterDTO getByUserId(String token, int userId) throws JsonProcessingException {
        var text = restAuthCall.setUrl(String.format("%s%d", URL, userId))
                .get(token);
        return new ObjectMapper().readValue(text, new TypeReference<>() {
        });
    }

    public void deleteByUserId(String token, int userId) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        restAuthCall.setUrl(String.format("%sdelete/%d", URL, userId)).delete(
                token,
                mapper.writeValueAsString(userId)
        );
    }
}
