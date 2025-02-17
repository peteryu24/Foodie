package com.sparta.tl3p.backend.domain.member.dto;

import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.member.enums.MemberStatus;
import com.sparta.tl3p.backend.domain.member.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private Long memberId;
    private String username;
    private String email;
    private String nickname;
    private String address;
    private Role role;
    private MemberStatus status;
    private LocalDateTime joinDate;

    public MemberResponseDto(Member member){
        this.memberId = member.getMemberId();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.address = member.getAddress();
        this.role = member.getRole();
        this.status = member.getStatus();
        this.joinDate = member.getJoinDate();
    }

}
