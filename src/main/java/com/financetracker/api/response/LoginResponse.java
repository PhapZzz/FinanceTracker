package com.financetracker.api.response;

import com.financetracker.api.entity.Role;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;

    private Instant expiresToken;

}
