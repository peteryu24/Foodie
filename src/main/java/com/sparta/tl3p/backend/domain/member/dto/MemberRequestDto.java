package com.sparta.tl3p.backend.domain.member.dto;

import com.sparta.tl3p.backend.domain.member.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;
    private String nickname;
    private String address;
    private Role role;

}
