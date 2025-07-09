package com.microsservices.Notification_Service.service;


import com.microsservices.Notification_Service.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final TwilioConfig twilioConfig;

    public NotificationService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    // método será executado assim que o serviço for criado
    @PostConstruct
    public void initTwilio() {
        Twilio.init(
                twilioConfig.getAccountSid(),
                twilioConfig.getAuthToken()
        );
    }

    public void sendWhatsAppMessage(String toPhoneNumber, String textMessage) {
        // formata o número de destino como texto
        String formattedToNumber = "whatsapp:+55" + toPhoneNumber;

        // objetos PhoneNumber
        PhoneNumber destination = new PhoneNumber(formattedToNumber);
        PhoneNumber sender = new PhoneNumber(twilioConfig.getWhatsappNumber());

        // passa os OBJETOS para o método creator
        Message.creator(
                        destination, // <-- CORRIGIDO! Passando um objeto PhoneNumber
                        sender,
                        textMessage)
                .create();
    }
}
