package com.project.medconnect.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private int userId;
    private String userName;
    private String email;
    private String role;
}
