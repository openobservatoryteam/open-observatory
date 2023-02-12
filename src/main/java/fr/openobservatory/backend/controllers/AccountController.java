package fr.openobservatory.backend.controllers;


import fr.openobservatory.backend.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import

@AllArgsConstructor
@Controller
public class AccountController {

    private AccountService accountService;

    @PostMapping("/save")
    public String register(@ModelAttribute("loginDto")) {

    }



}
