package com.example.ResumeGeneratorApplication.dto;

import com.example.ResumeGeneratorApplication.entity.enums.Gender;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private Gender gender;
}
