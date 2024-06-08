package ru.checkdev.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.client.DescServiceClient;
import ru.checkdev.notification.domain.dto.CategoryDto;
import ru.checkdev.notification.telegram.config.TgConfig;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private static final String ID = "id";
    private static final String URL_DESC_CATEGORIES = "/categories/";
    private static final String URL_DESC_CATEGORY = "/category/%s";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final DescServiceClient descServiceClient;

    public List<CategoryDto> getCategories() {
        List<CategoryDto> result = new ArrayList<>();
        try {
            result = descServiceClient.doGetAll(URL_DESC_CATEGORIES)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.error("WebClient doGet error: {}", e.getMessage());
        }
        return result;
    }

    public boolean categoryExists(String id) {
        boolean exists = false;
        try {
            Object result = descServiceClient.doGet(String.format(URL_DESC_CATEGORY, id), Object.class).block();
            var mapObject = tgConfig.getObjectToMap(result);
            log.info("category: [{}]", result);
            if (mapObject.containsKey(ID)) {
                exists = true;
            }
        } catch (Exception e) {
            log.error("WebClient doGet error: {}", e.getMessage());
        }
        return exists;
    }
}
