package com.project.medconnect.appointment;

import com.project.medconnect.dto.AppointmentRequest;
import com.project.medconnect.dto.AppointmentResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
 private  final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
@PostMapping
@PreAuthorize("hasRole('PATIENT')")
public ResponseEntity<AppointmentResponse> book (@Valid @RequestBody AppointmentRequest appointmentRequest, Authentication authentication) throws BadRequestException {
     String patientEmail = authentication.getName();
    AppointmentResponse response = appointmentService.book(patientEmail , appointmentRequest);
    return ResponseEntity.status(201).body(response);
}
}
