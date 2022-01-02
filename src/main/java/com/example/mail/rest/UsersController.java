package com.example.mail.rest;

import com.example.mail.OnRegistrationCompleteEvent;
import com.example.mail.dto.UserDto;
import com.example.mail.model.User;
import com.example.mail.model.VerificationToken;
import com.example.mail.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UsersController {

    ApplicationEventPublisher eventPublisher;

    UsersService usersService;

    MessageSource messageSource;

    public UsersController(ApplicationEventPublisher eventPublisher, UsersService usersService, MessageSource messageSource) {
        this.eventPublisher = eventPublisher;
        this.usersService = usersService;
        this.messageSource = messageSource;
    }

    @PostMapping(value = "/registration", produces = "application/json")
    public Map<String, Object> registerUserAccount(
            @RequestBody @Valid UserDto userDto,
            HttpServletRequest request, Errors errors) {
        HashMap<String, Object> modelMap = new HashMap<>();

        User registered = usersService.register(userDto);

        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                request.getLocale(), appUrl));

        modelMap.put("success", true);
        modelMap.put("user", userDto);

        return modelMap;
    }

    @GetMapping("/registrationConfirm")
    public Map<String, Object> confirmRegistration (WebRequest request, @RequestParam("token") String token) {
        HashMap<String, Object> modelMap = new HashMap<>();

        VerificationToken verificationToken = usersService.getVerificationToken(token);

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if(((verificationToken.getExpiryDate().getTime())-cal.getTime().getTime()) <= 0) {
            String messageValue = "Token has expired";
            modelMap.put("message", messageValue);
            return modelMap;
        }

        user.setEnabled(true);
        usersService.saveRegisteredUser(user);
        modelMap.put("message", "successful registration");
        return modelMap;

    }


}
