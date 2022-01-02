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
import java.util.Locale;

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
    public void registerUserAccount(
            @RequestBody @Valid UserDto userDto,
            HttpServletRequest request, Errors errors, Model modelMap) {

        User registered = usersService.register(userDto);

        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                request.getLocale(), appUrl));

        modelMap.addAttribute("success", true);
        modelMap.addAttribute("user", userDto);
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration (WebRequest request, Model model, @RequestParam("token") String token) {
        Locale locale = request.getLocale();

        VerificationToken verificationToken = usersService.getVerificationToken(token);

        if(verificationToken == null) {
            String message = messageSource.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang="+locale.getLanguage();
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if(((verificationToken.getExpiryDate().getTime())-cal.getTime().getTime()) <= 0) {
            String messageValue = messageSource.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", messageValue);
            return "redirect:/badUser.html?lang="+locale.getLanguage();
        }

        user.setEnabled(true);
        usersService.saveRegisteredUser(user);
        return "redirect:/login.html?lang="+request.getLocale().getLanguage();

    }


}
