package com.team9.jobbotdari.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequestDto {
    private String name;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$",
            message = "비밀번호는 8자 이상이며, 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    private String passwordConfirm;
}