package com.Tigggle.Config;

import com.Tigggle.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConf {

    @Autowired
    UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        HttpSessionRequestCache requestCache=new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);

        http.requestCache(rq->rq.requestCache(requestCache))
                .authorizeHttpRequests(
                        ar -> ar
//                                .requestMatchers("/*","/user/**")// 요청 매처를 사용하여 요청을 매칭
//                                .permitAll() // requestMatchers에 작성된 주소요청에대해 모두 허용 - 인증 노!
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().permitAll()  // 모든 요청에 대해
                               // .authenticated() // 인증 해야 한다. - 로그인 해야함
                )
                .formLogin(
                        form -> form
                                .loginPage("/user/signIn")  // 커스텀 로그인 페이지 주소
                                .defaultSuccessUrl("/") // 로그인 성공하면 어디로 ?
                                .usernameParameter("accessId") //로그인 할때 계정명 input name
                                .failureUrl("/user/signIn/error")// 로그인 실패시 어디로?
                                .permitAll()  // 로그인 페이지 에 대한 모두가 접근할수 있게 허용
                )
                .logout(out->out
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );
//        http.formLogin(Customizer.withDefaults());// 기본 로그인 페이지 비활성화

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
