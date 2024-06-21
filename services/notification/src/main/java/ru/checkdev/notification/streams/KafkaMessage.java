package ru.checkdev.notification.streams;

public interface KafkaMessage {
    String getQueue();
    Object getPayload();
}
