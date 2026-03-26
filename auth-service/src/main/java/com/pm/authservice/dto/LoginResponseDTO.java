package com.pm.authservice.dto;

public class LoginResponseDTO {
    //jwt token, once initialized, can't be mutated
    private final String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }
    
    //json format
    public String getToken() {
        return token;
    }

}
