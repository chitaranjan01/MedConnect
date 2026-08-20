package com.project.medconnect.service;

import com.project.medconnect.domain.entity.Doctor;
import com.project.medconnect.domain.entity.Patient;
import com.project.medconnect.domain.entity.Role;
import com.project.medconnect.domain.entity.User;
import com.project.medconnect.dto.LoginRequest;
import com.project.medconnect.dto.LoginResponse;
import com.project.medconnect.dto.RegisterDoctorRequest;
import com.project.medconnect.dto.RegisterPatient;
import com.project.medconnect.dto.UserResponse;
import com.project.medconnect.repository.DoctorRepository;
import com.project.medconnect.repository.PatientRepository;
import com.project.medconnect.repository.UserRepository;
import com.project.medconnect.jwt.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AuthService(DoctorRepository doctorRepository,
                       PatientRepository patientRepository,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public UserResponse registerDoctor(RegisterDoctorRequest registerDoctorRequest) {
        if (userRepository.findByEmail(registerDoctorRequest.getEmail()).isPresent()) {
            throw new BadCredentialsException("email already registered");
        }

        Doctor doctor = new Doctor();
        doctor.setEmail(registerDoctorRequest.getEmail());
        doctor.setPasswordHash(passwordEncoder.encode(registerDoctorRequest.getPassword()));
        doctor.setUserName(registerDoctorRequest.getUsername());
        doctor.setSpecialty(registerDoctorRequest.getSpecialty());
        doctor.setLicenseNumber(registerDoctorRequest.getLicenseNumber());
        doctor.setRole(Role.DOCTOR);

        Doctor saved = doctorRepository.save(doctor);

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(saved.getUserId());
        userResponse.setEmail(saved.getEmail());
        userResponse.setUserName(saved.getUserName());
        userResponse.setRole(saved.getRole().toString());
        return userResponse;
    }

    public UserResponse registerPatient(RegisterPatient request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadCredentialsException("email already registered");
        }

        Patient patient = new Patient();
        patient.setEmail(request.getEmail());
        patient.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        patient.setUserName(request.getUsername());
        patient.setBloodgroup(request.getBloodgroup());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setRole(Role.PATIENT);

        Patient saved = patientRepository.save(patient);

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(saved.getUserId());
        userResponse.setEmail(saved.getEmail());
        userResponse.setUserName(saved.getUserName());
        userResponse.setRole(saved.getRole().toString());
        return userResponse;
    }

    public LoginResponse login(LoginRequest request){
        User user =userRepository.findByEmail(request.getEmail()).orElseThrow(()->new BadCredentialsException("invalid enail or password"));

        if (!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
            throw new BadCredentialsException("invalid enail or password");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        LoginResponse response = new LoginResponse();
        response.setAccess_token(token);
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
         return response;
    }

}
