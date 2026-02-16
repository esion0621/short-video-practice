package com.video.backend.kafka;

import com.video.backend.entity.UserAction;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BehaviorProducer {

    private static final String TOPIC = "user-behavior-topic";
    private final KafkaTemplate<String, UserAction> kafkaTemplate;  

    public void sendBehavior(UserAction action) {
        kafkaTemplate.send(TOPIC, String.valueOf(action.getUserId()), action);
    }
}
