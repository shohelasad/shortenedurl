package com.demo.app.shortenurl.controllers;

import com.demo.app.shortenurl.payload.request.LoginRequest;
import com.demo.app.shortenurl.payload.request.SignupRequest;
import com.demo.app.shortenurl.payload.response.JwtResponse;
import com.demo.app.shortenurl.payload.response.MessageResponse;
import com.demo.app.shortenurl.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	private AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signin")
	public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.authenticateUser(loginRequest));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		//TODO: implement signup
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
