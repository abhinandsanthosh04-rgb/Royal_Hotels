package com.royalhotels.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AdminAuthController - REST API Controller for Admin Authentication.
 *
 * Exposes:
 *  - POST /api/admin/auth/login : Authenticates administrator and returns JWT token
 *  - GET  /api/admin/auth/me    : Returns authenticated administrator profile
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    /**
     * POST /api/admin/auth/login
     *
     * Authenticates an admin using username and password.
     * Returns a signed JWT token on success or HTTP 401 on failure.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(AdminLoginResponse.error("Username and password are required."));
        }

        String username = request.getUsername().trim();
        String password = request.getPassword();

        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AdminLoginResponse.error("Invalid administrator credentials."));
        }

        Admin admin = adminOpt.get();
        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AdminLoginResponse.error("Invalid administrator credentials."));
        }

        String token = jwtTokenService.generateToken(admin.getUsername(), admin.getFullName(), admin.getRole());

        AdminLoginResponse response = AdminLoginResponse.success(
                token,
                admin.getUsername(),
                admin.getFullName(),
                admin.getRole(),
                jwtTokenService.getExpirationMs()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/auth/me
     *
     * Protected endpoint: Returns current admin details.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Unauthorized"));
        }

        String username = auth.getName();
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Admin account not found"));
        }

        Admin admin = adminOpt.get();
        Map<String, Object> profile = new HashMap<>();
        profile.put("adminId", admin.getAdminId());
        profile.put("username", admin.getUsername());
        profile.put("fullName", admin.getFullName());
        profile.put("role", admin.getRole());
        profile.put("createdAt", admin.getCreatedAt());

        return ResponseEntity.ok(profile);
    }
}
