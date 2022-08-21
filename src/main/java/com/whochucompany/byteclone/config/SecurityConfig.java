package com.whochucompany.byteclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 설정파일을 만들거나 bean 등록을 위한 어노테이션
@EnableWebSecurity //SecurityFilterChain 등 WebSecurity 에 필요한 거 어노테이션으로 가져옴
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/*").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }
}
