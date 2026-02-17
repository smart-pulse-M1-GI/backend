package com.smartpulse.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class testController {

    @GetMapping("/test")
    public String test() {
        return "Test OK – si tu vois ça, le JWT marche !";
    }
}
