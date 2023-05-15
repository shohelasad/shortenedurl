package com.demo.app.shortenurl.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(max = 50)
    @Email
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private Set<String> roles = new HashSet<>();

    public SignupRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
