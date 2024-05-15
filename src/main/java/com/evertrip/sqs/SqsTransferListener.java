package com.evertrip.sqs;

import com.evertrip.post.dto.sqs.PostLogDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsTransferListener {

    @SqsListener(value = "${cloud.aws.sqs.queue-name}",factory = "defaultSqsListenerContainerFactory")
    public void messageListener(PostLogDto message) {
        System.out.println("Listener : " + message);
    }

}