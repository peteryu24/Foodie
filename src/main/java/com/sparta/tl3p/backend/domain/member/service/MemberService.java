package com.sparta.tl3p.backend.domain.member.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.member.dto.MemberRequestDto;
import com.sparta.tl3p.backend.domain.member.dto.MemberResponseDto;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.member.enums.Role;
import com.sparta.tl3p.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //회원가입
    public MemberResponseDto signupMember(MemberRequestDto requestDto) {
        // 아이디 중복
        if (memberRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATE);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 회원 생성
        Member member = new Member();
        member.setUsername(requestDto.getUsername());
        member.setPassword(encodedPassword);
        member.setEmail(requestDto.getEmail());
        member.setNickname(requestDto.getNickname());
        member.setAddress(requestDto.getAddress());
        member.setRole(requestDto.getRole() != null ? requestDto.getRole() : Role.CUSTOMER);

        // 회원 저장
        Member savedMember = memberRepository.save(member);

        return new MemberResponseDto(savedMember);

    }

    // 회원조회
    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto(member);
    }

    // 회원정보 수정
    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (requestDto.getNickname() != null) {
            member.setNickname(requestDto.getNickname());
        }

        if (requestDto.getEmail() != null) {
            member.setEmail(requestDto.getEmail());
        }

        if (requestDto.getAddress() != null) {
            member.setAddress(requestDto.getAddress());
        }

        return new MemberResponseDto(member);
    }

    // 회원탈퇴
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }

    // 회원 전체 조회
    public List<MemberResponseDto> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(MemberResponseDto::new).collect(Collectors.toList());
    }

}
