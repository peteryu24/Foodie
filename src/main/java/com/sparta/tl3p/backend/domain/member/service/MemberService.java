package com.sparta.tl3p.backend.domain.member.service;

import com.sparta.tl3p.backend.domain.member.dto.MemberRequestDto;
import com.sparta.tl3p.backend.domain.member.dto.MemberResponseDto;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.member.enums.Role;
import com.sparta.tl3p.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //회원가입
    public MemberResponseDto signupMember(MemberRequestDto requestDto){
        // 아이디 중복
        if(memberRepository.findByUsername(requestDto.getUsername()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화
        String encordedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 회원 생성
        Member member = new Member();
        member.setUsername(requestDto.getUsername());
        member.setPassword(encordedPassword);
        member.setEmail(requestDto.getEmail());
        member.setNickname(requestDto.getNickname());
        member.setAddress(requestDto.getAddress());
        member.setRole(requestDto.getRole() != null ? requestDto.getRole() : Role.CUSTOMER);

        // 회원 저장
        Member savedMember = memberRepository.save(member);

        return new  MemberResponseDto(savedMember);
    }

    // 회원조회
    public MemberResponseDto getMemberById(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return new MemberResponseDto(member);
    }

}
