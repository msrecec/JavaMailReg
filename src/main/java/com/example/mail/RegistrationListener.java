package com.example.mail;

import com.example.mail.model.User;
import com.example.mail.service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private UsersService usersService;

    private MessageSource messageSource;

    private JavaMailSender javaMailSender;

    public RegistrationListener(UsersService usersService, MessageSource messageSource, JavaMailSender javaMailSender) {
        this.usersService = usersService;
        this.messageSource = messageSource;
        this.javaMailSender = javaMailSender;
    }



    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {

        User user = event.getUser();

        String token = UUID.randomUUID().toString();

        usersService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();

        String subject = "Registration Confirmation";

        String confirmationUrl = event.getAppUrl()+"/user/registrationConfirm?token="+token;
        String message = "registration is great";

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message+"\r\n"+"http://localhost:8080"+confirmationUrl);
        javaMailSender.send(email);
    }

}
