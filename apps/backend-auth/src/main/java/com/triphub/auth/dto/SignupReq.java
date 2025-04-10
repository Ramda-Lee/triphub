package com.triphub.auth.dto;

import lombok.Getter;

@Getter
public class SignupReq {
    private String email;
    private String password;
    private String name;
}
