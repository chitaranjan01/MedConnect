package com.project.medconnect.controler;

import com.project.medconnect.dto.LoginRequest;
import com.project.medconnect.dto.LoginResponse;
import com.project.medconnect.dto.RegisterDoctorRequest;
import com.project.medconnect.dto.RegisterPatient;
import com.project.medconnect.dto.UserResponse;
import com.project.medconnect.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControl {
private final AuthService authService;
public AuthControl(AuthService authService){
    this.authService=authService;
}
    @PostMapping("/register/doctor")
    public ResponseEntity<UserResponse>registerDoctor(@Valid @RequestBody RegisterDoctorRequest request) {
        UserResponse response = authService.registerDoctor(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/register/patient")
    public ResponseEntity<UserResponse> registerPatient(@Valid @RequestBody RegisterPatient request) {
        UserResponse response = authService.registerPatient(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>login(@Valid @RequestBody LoginRequest request){
    return   ResponseEntity.ok().body(authService.login(request));
    }

}
