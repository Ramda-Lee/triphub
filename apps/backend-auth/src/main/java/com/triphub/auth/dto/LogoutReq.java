package com.triphub.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutReq {
    private String accessToken;
    private String refreshToken;
}
