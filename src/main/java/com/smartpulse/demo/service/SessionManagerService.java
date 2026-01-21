package com.smartpulse.demo.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Getter
@Service
public class SessionManagerService {
    private Long activeSessionId;

    public void stopSession() {
        this.activeSessionId = null;
    }
}