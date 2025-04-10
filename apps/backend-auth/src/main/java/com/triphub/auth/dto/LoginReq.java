package com.triphub.auth.dto;

import lombok.Getter;

@Getter
public class LoginReq {
    private String email;
    private String password;
}
