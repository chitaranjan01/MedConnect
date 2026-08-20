package com.project.medconnect.controler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MeController {
     @GetMapping("/me")
    public String me(Authentication authentication) {
         return "you are logged in as "+ authentication.getName() + "with role" + authentication.getAuthorities();
     }

     @GetMapping("/doctor-only")
    @PreAuthorize("hasRole('DOCTOR')")
    public String doctorOnly() {
         return "only doctor can see this page";
     }
     @GetMapping("/patient-only")
    @PreAuthorize("hasRole('PATIENT')")
    public String patientOnly() {
         return "only patient can see this page";
     }
}
