package com.triphub.auth.controller;

import com.triphub.auth.dto.LoginReq;
import com.triphub.auth.dto.LoginRes;
import com.triphub.auth.dto.SignupReq;
import com.triphub.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupReq req) {
        authService.signup(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.reissue(refreshToken));
    }
}
