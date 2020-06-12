package com.example.DistributedTransaction.api.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class JMSConsumer {
    private static final String JMS_DESTINATION = "queue";

    @JmsListener(destination = JMS_DESTINATION)
    public void messageListener(String message) {
        System.out.println("message : " + message);
    }

}
