package com.whochucompany.byteclone.service;

import com.whochucompany.byteclone.domain.member.dto.MemberRequestDto;
import com.whochucompany.byteclone.domain.member.dto.MemberResponseDto;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    MemberResponseDto saveUser(MemberRequestDto memberRequestDto);
    MemberResponseDto getMember(String username);

    ResponseEntity<?> emailExist(String email);
}
