package com.auto.car.api.service;

import com.auto.car.api.service.dto.SendMailRequest;

public interface SendMailService {
    void sendMail(SendMailRequest sendMailRequest);
}

