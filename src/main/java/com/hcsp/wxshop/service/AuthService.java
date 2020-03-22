package com.hcsp.wxshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final VerificationCodeCheckService verificationCodeCheckService;
    private final SmsCodeService smsCodeService;

    @Autowired
    public AuthService(UserService userService,
                       VerificationCodeCheckService verificationCodeCheckService,
                       SmsCodeService smsCodeService) {
        this.userService = userService;
        this.verificationCodeCheckService = verificationCodeCheckService;
        this.smsCodeService = smsCodeService;
    }

    public void sendVerificationCode(String tel) {
        userService.createUserIfNotExist(tel);
        String correctCode = smsCodeService.sendSmsCode(tel);
        verificationCodeCheckService.addCode(tel, correctCode);
    }
}
