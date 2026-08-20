package com.project.medconnect.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String access_token;
    private String email;
    private String role;
}
