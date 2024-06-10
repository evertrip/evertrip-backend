package com.evertrip.security.auth;

import com.evertrip.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// UserDetailsService를 implements하고 UserRepository를 주입받는다.
// loadUserByUsername 메소드를 오버라이드해서 로그인 시에 DB에서 유저정보와 권한정보를 가져오게 된다.
// 해당 정보를 기반으로 userdetails.User 객체를 생성해서 리턴한다.
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {
        return memberRepository.findByEmail(email)
                .map(member ->  new MemberDetails(member))
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
    }
}
