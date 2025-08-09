package com.example.ResumeGeneratorApplication.controller;


import com.example.ResumeGeneratorApplication.dto.LoginDto;
import com.example.ResumeGeneratorApplication.dto.LoginResponseDto;
import com.example.ResumeGeneratorApplication.dto.SignUpRequestDto;
import com.example.ResumeGeneratorApplication.dto.UserDto;
import com.example.ResumeGeneratorApplication.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Create a new account", tags = {"Auth"})
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        return new ResponseEntity<>(authService.signUp(signUpRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login request", tags = {"Auth"})
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto,
                                                  HttpServletRequest httpServletRequest,
                                                  HttpServletResponse httpServletResponse){
        String[] tokens = authService.login(loginDto);
        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setPath("/");
        cookie.setMaxAge(6 * 30 * 24 * 60 * 60);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }


    @PostMapping("/logout")
    @Operation(summary = "Logout request", tags = {"Auth"})
    // Clear the refreshToken cookie
    public ResponseEntity<LoginResponseDto> logout(@RequestBody LoginDto loginDto,
                                                  HttpServletRequest httpServletRequest,
                                                  HttpServletResponse httpServletResponse){
        String[] tokens = authService.login(loginDto);
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }


    //1. Get Cookies
    //2. check whether cookies are empty/null, if so, then throw error
    //3. else get refresh token
    //4. get access token from refresh token
    //5. send access token
    @PostMapping("/refresh")
    @Operation(summary = "refresh the JWT with refresh token",tags = {"Auth"})
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request){

        Cookie[] cookies = request.getCookies();

        if(cookies==null){
            throw new AuthenticationServiceException("No refresh token found");
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findAny()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));

        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }
}
