package com.morning.security.user;

import com.morning.security.config.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final JwtService jwtService;
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    public ProfileInfoDTO getUserInfo(String token){
        User user = repository.findByUsername(jwtService.extractUsername(token.substring(7))).get();
        if(user == null){
            log.error(String.format("User (%s) was not found", jwtService.extractUsername(token)));
            throw new RuntimeException(String.format("User (%s) was not found", jwtService.extractUsername(token)));
        }
        return ProfileInfoDTO.builder()
                .telegramId(user.getTelegramId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public ProfileInfoDTO updateUserInfo(String token, ProfileInfoDTO dto){
        User user = repository.findByUsername(jwtService.extractUsername(token.substring(7))).get();
        if(user == null){
            log.error(String.format("User (%s) was not found", jwtService.extractUsername(token)));
            throw new RuntimeException(String.format("User (%s) was not found", jwtService.extractUsername(token)));
        }
        user.setEmail(dto.getEmail() == null ? user.getEmail() : dto.getEmail());
        user.setTelegramId(dto.getTelegramId() != null ? dto.getTelegramId() : user.getTelegramId());
        try {
            repository.save(user);
            return ProfileInfoDTO.builder()
                    .telegramId(user.getTelegramId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Exception, saving updated user!");
        }
    }
}
