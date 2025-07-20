package com.Tigggle.Config;

import com.Tigggle.Service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.List;

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
                                .requestMatchers("/css/**", "/javascript/**", "/images/**").permitAll() // 정적 리소스
                                .requestMatchers("/","/user/**")// 요청 매처를 사용하여 요청을 매칭
                                .permitAll() // requestMatchers에 작성된 주소요청에대해 모두 허용 - 인증 노!
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()  // 모든 요청에 대해
                               // .authenticated() // 인증 해야 한다. - 로그인 해야함
                )
                .formLogin(
                        form -> form
                                .loginPage("/user/signIn")  // 커스텀 로그인 페이지 주소
                                .loginProcessingUrl("/user/signIn") // formLogin이 담당할 가짜 URL (실제 사용 안 함)
                                .usernameParameter("accessId") //로그인 할때 계정명 input name

                                .successHandler((request, response, authentication) -> {
                                    response.setStatus(HttpServletResponse.SC_OK);
                                    response.setContentType("application/json");
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write("{\"success\": true}");
                                }) // 로그인 성공하면 어디로 ?
                                .failureHandler((request, response, exception) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write("{\"success\": false, \"message\": \"아이디나 비밀번호가 잘못되었습니다.\"}");
                                })// 로그인 실패시 어디로?
                                .permitAll()  // 로그인 페이지 에 대한 모두가 접근할수 있게 허용
                )
                .logout(out->out
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("a-very-secret-key")
                        .rememberMeParameter("loginKeep") // checkbox name과 일치
                        .tokenValiditySeconds(60 * 60 * 24 * 7) // 7일
                        .userDetailsService(userService)
                );

//        http.formLogin(Customizer.withDefaults());// 기본 로그인 페이지 비활성화

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        //repo.setCreateTableOnStartup(true);
        return repo;
    }
}
