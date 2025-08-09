package com.example.ResumeGeneratorApplication.service;

import com.example.ResumeGeneratorApplication.entity.User;
import com.example.ResumeGeneratorApplication.exception.ResourceNotFoundException;
import com.example.ResumeGeneratorApplication.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
//    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found with email:"+username));
    }

    @Override
    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id));
    }


}
