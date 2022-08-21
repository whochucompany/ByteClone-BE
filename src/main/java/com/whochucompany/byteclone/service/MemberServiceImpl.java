package com.whochucompany.byteclone.service;

import com.whochucompany.byteclone.domain.member.Member;
import com.whochucompany.byteclone.domain.member.dto.MemberRequestDto;
import com.whochucompany.byteclone.domain.member.dto.MemberResponseDto;
import com.whochucompany.byteclone.logging.Logging;
import com.whochucompany.byteclone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<?> emailExist(String email) {
        boolean result = memberRepository.existsByEmail(email);
        if (result) { // 결과가 존재하면 참 --> result 가 true 면...
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.entry("message", "등록된 이메일입니다."));
        }
        else {
            return ResponseEntity.ok().body(Map.entry("message", "사용가능한 이메일입니다."));
        }
    }

    @Transactional
    @Override
    public MemberResponseDto saveUser(MemberRequestDto userRequestDto) {

        // 새로운 멤버 객체를 생성하자~
        Member member = Member.builder()
                .email(userRequestDto.getEmail())
                .username(userRequestDto.getUsername())
                .password(userRequestDto.getPassword())
                .build();

        try {
            // 회원 저장
            memberRepository.save(member);
            log.info("msg : {}님 회원가입이 성공", member.getUsername());

        } catch (Exception e) {
            String error = Logging.getPrintStackTrace(e);
            log.error(error);
        }
        // Todo :: 트랜잭션 전 @id 값을 어떻게 가져오는지.. insert query 를 날리는지 확인.
        // return 할 MemberResponseDto
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .build();
    }

    @Override
    public MemberResponseDto getMember(String username) {

        Optional<Member> member = memberRepository.findByUsername(username);

        return null;
    }


}
