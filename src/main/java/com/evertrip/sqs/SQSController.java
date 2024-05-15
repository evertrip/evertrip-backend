package com.evertrip.sqs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SQSController {

    private final SqsMessageSender sqsMessageSender;


    @PostMapping("/test/message")
    public void sendMessage(@RequestBody String message) {
        sqsMessageSender.sendMessage(message);
    }


}
