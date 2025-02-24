package com.sparta.tl3p.backend.domain.member.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.common.util.JwtTokenProvider;
import com.sparta.tl3p.backend.domain.member.dto.LoginRequestDto;
import com.sparta.tl3p.backend.domain.member.dto.LoginResponseDto;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

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

    // 회원 단일조회
    public MemberResponseDto getMemberById(Long memberId, String token) {
        Long currentMemberId = getCurrentMemberId(token);
        if (!currentMemberId.equals(memberId) && !isMaster(currentMemberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto(member);
    }

    // 회원정보 수정
    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberRequestDto requestDto, String token) {

        Long currentMemberId = getCurrentMemberId(token);

        if (!currentMemberId.equals(memberId) && !isMaster(currentMemberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

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
    public void deleteMember(Long memberId, String token) {

        Long currentMemberId = getCurrentMemberId(token);

        if (!currentMemberId.equals(memberId) && !isMaster(currentMemberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }

    private boolean isMaster(Long memberId) {
        return memberRepository.findById(memberId)
                .map(member -> member.getRole().equals(Role.MASTER))
                .orElse(false);
    }

    private Long getCurrentMemberId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.INVALID_JWT_TOKEN);
        }

        // "Bearer " 제거 후 토큰 값만 추출
        String pureToken = token.substring(7).trim();

        return jwtTokenProvider.getMemberIdFromToken(pureToken);
    }

    // 회원 전체 조회
    public List<MemberResponseDto> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(MemberResponseDto::new).collect(Collectors.toList());
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto requestDto) {

        // 사용자 조회
        Member member = memberRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Access Token 과 Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());


        redisService.saveRefreshToken(member.getMemberId(), refreshToken, 7 * 24 * 60 * 60 * 1000L);// 7일

        return new LoginResponseDto(accessToken, refreshToken);
    }

    // 로그아웃
    public void logout(String token) {
        // "Bearer " 제거 후 토큰 값만 추출
        String pureToken = token.substring(7).trim();

        if(pureToken.isEmpty()){
            throw new BusinessException(ErrorCode.INVALID_JWT_TOKEN);
        }

        Long memberId = jwtTokenProvider.getMemberIdFromToken(pureToken); // 토큰에서 memberId 추출

        redisService.deleteRefreshToken(memberId); // Redis에서 Refresh Token 삭제
    }
}
