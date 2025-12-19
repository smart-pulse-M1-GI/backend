package com.smartpulse.demo.controller;

import com.smartpulse.demo.Service.JwtService;
import com.smartpulse.demo.Service.UserService;
import com.smartpulse.demo.model.DTO.AuthResponse;
import com.smartpulse.demo.model.DTO.UserProfileResponse;
import com.smartpulse.demo.model.Enum.Role;
import com.smartpulse.demo.model.entity.Medecin;
import com.smartpulse.demo.model.entity.User;
import com.smartpulse.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // user est chargé grâce au filtre JWT
        if (userRepository.findByMail(userDetails.getUsername()).isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        User user = userRepository.findByMail(userDetails.getUsername()).get();
        UserProfileResponse response = userService.getUserInfo(user);
        return ResponseEntity.ok(response);
    }

}
