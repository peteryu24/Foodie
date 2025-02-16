package com.sparta.tl3p.backend.common.security;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Member member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
            return new UserDetailsImpl(member);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException(ErrorCode.UNEXPECTED_ERROR.getMessage());
        }
    }
}
