package com.triphub.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupReq {
    private String email;
    private String password;
    private String name;
}
