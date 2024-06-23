package com.morning.mailnotification.kafkaListeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.mailnotification.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {
    private ObjectMapper objectMapper = new ObjectMapper();
    private final MailService mailService;
    @KafkaListener(
            topics = "mail-verification-topic",
            groupId = "confirmation"
    )
    void listener(String data){
        Map<String, String> event = null;
        try {
            event = objectMapper.readValue(data, Map.class);
            switch(event.get("action")){
                case "VERIFY_EMAIL":{
                    mailService.sendVerificationMail(event);
                    break;
                }
            }
        } catch(Exception e){
            log.error(e.toString());
        }
    }

//    @KafkaListener(
//            topics = "notification",
//            groupId = "confirmation1"
//    )
//    void listener2(String data){
//        System.out.println(data + "recieved");
//    }
}
