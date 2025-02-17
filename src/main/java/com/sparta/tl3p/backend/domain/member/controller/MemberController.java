package com.sparta.tl3p.backend.domain.member.controller;

import com.sparta.tl3p.backend.domain.member.dto.MemberRequestDto;
import com.sparta.tl3p.backend.domain.member.dto.MemberResponseDto;
import com.sparta.tl3p.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
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
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable Long memberId){
        return ResponseEntity.ok(memberService.getMemberById(memberId));
    }

}
