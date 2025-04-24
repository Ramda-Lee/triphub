package com.triphub.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignupReq {
    private String email;
    private String password;
    private String name;
}
