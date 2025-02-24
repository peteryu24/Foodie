package com.sparta.tl3p.backend.domain.member.controller;

import com.sparta.tl3p.backend.domain.member.dto.LoginRequestDto;
import com.sparta.tl3p.backend.domain.member.dto.LoginResponseDto;
import com.sparta.tl3p.backend.domain.member.dto.MemberRequestDto;
import com.sparta.tl3p.backend.domain.member.dto.MemberResponseDto;
import com.sparta.tl3p.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(@Valid @RequestBody MemberRequestDto requestDto){
        return ResponseEntity.ok(memberService.signupMember(requestDto));

    }

    //회원조회
    @GetMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberResponseDto> getMemberById(
            @PathVariable("memberId") Long memberId,
            @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(memberService.getMemberById(memberId, token));
    }

    //회원정보수정
    @PutMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberResponseDto> updateMember(
            @PathVariable("memberId") Long memberId,
            @RequestBody MemberRequestDto requestDto,
            @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(memberService.updateMember(memberId, requestDto, token));
    }

    // 회원탈퇴
    @DeleteMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteMember(
            @PathVariable("memberId") Long memberId,
            @RequestHeader("Authorization") String token){
        memberService.deleteMember(memberId, token);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    // 회원전체조회
    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    public ResponseEntity<List<MemberResponseDto>> getAllMembers(){
        List<MemberResponseDto> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto requestDto){
        return  ResponseEntity.ok(memberService.login(requestDto));
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String token) {
        memberService.logout(token);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
