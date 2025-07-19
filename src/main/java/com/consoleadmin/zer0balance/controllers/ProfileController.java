package com.consoleadmin.zer0balance.controllers;


import com.consoleadmin.zer0balance.dto.ProfileDTO;
import com.consoleadmin.zer0balance.dto.AuthDTO;
import com.consoleadmin.zer0balance.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;


    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Activated");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Activated");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login (@RequestBody AuthDTO authDTO) {
        try{
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Zer0Money Account is not active , Please activate your account first"
                ));
            }
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Error while logging you in"
            ));
        }
    }

    @GetMapping("/test")
    public String testProtectedRoute(){
        return "consoleAdmin";
    }
}
