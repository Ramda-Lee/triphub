package com.triphub.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LogoutReq {
    private String accessToken;
    private String refreshToken;
}
