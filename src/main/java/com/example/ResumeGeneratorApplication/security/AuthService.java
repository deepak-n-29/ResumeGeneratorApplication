package com.example.ResumeGeneratorApplication.security;

import com.example.ResumeGeneratorApplication.dto.LoginDto;
import com.example.ResumeGeneratorApplication.dto.SignUpRequestDto;
import com.example.ResumeGeneratorApplication.dto.UserDto;
import com.example.ResumeGeneratorApplication.entity.User;
import com.example.ResumeGeneratorApplication.entity.enums.Role;
import com.example.ResumeGeneratorApplication.exception.ResourceNotFoundException;
import com.example.ResumeGeneratorApplication.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {
        User user = userRepo.findByEmail(signUpRequestDto.getEmail()).orElse(null);
        if (user != null) {
            throw new BadCredentialsException("Cannot Sign up, User already exists with email" + signUpRequestDto.getEmail());
        }

        User newUser = modelMapper.map(signUpRequestDto, User.class);
        newUser.setRoles(Set.of(Role.ADMIN, Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));

        newUser = userRepo.save(newUser);
        return modelMapper.map(newUser, UserDto.class);

    }

    public String[] login(LoginDto loginDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),
                        loginDto.getPassword()));

        User user = (User) authentication.getPrincipal();

        String[] arr = new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public String refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return jwtService.generateAccessToken(user);
    }
}
