package com.dbdevdeep.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class WebSecurityConfig {
	
	private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

  @Autowired
  public WebSecurityConfig(CustomLogoutSuccessHandler customLogoutSuccessHandler) {
      this.customLogoutSuccessHandler = customLogoutSuccessHandler;
  }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/assets/**", "/dbdevdeepcss/**", "/dbdevdeepjs/**", "/dist/**", "/docs/**", "/scss/**", "/login.js").permitAll()
                .requestMatchers("/signup").hasAnyAuthority("D3")
                .anyRequest().hasAnyAuthority("Y")  // 나머지 모든 요청은 인증 필요
            )
            // 로그인 설정
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("emp_id")
                .passwordParameter("emp_pw")
                .permitAll()  // 로그인 접근 혀용
                .successHandler(new MyLoginSuccessHandler())
                .failureHandler(new MyLoginFailureHandler())
            )
            // 로그아웃 설정
            .logout(logout -> logout
            		.logoutUrl("/logout")
            		 .logoutSuccessHandler(customLogoutSuccessHandler)
            		.permitAll()
            ); // 로그아웃 접근 허용
        
        // 웹소켓 관련 보안 설정 (추후 정리 필요)
        http.csrf(csrf ->csrf.ignoringRequestMatchers("/api/notifications"));

        return http.build();
    }
    

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적 자원 무시
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 패스워드 인코더
    }
}
