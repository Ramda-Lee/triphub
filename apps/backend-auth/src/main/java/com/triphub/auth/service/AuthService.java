package com.triphub.auth.service;

import com.triphub.auth.dto.LoginReq;
import com.triphub.auth.dto.LoginRes;
import com.triphub.auth.dto.SignupReq;

public interface AuthService {
    void signup(SignupReq req);

    LoginRes login(LoginReq req);

    String reissue(String refreshToken);
}
