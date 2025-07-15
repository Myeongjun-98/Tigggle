package com.Tigggle.Controller;

import com.Tigggle.DTO.LoginRequest;
import com.Tigggle.DTO.MemberFormDto;
import com.Tigggle.DTO.MyPageDto;
import com.Tigggle.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PersistentTokenRepository tokenRepository;

    // 내정보 변경
    @PostMapping("/user/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody MyPageDto dto,
                                           Principal principal) {
        try {
            String userId= principal.getName();
            userService.updateUserInfo(userId, dto);  // DB 저장
            return ResponseEntity.ok().body(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }


    // 프로필 변경
    @PostMapping("/user/profile/upload")
    public ResponseEntity<?> uploadProfile(@RequestParam("profile") MultipartFile file,
                                           Principal principal) {

        if(principal != null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인하슈");
        }
        try {
            String imageUrl = userService.saveProfileImage(file, principal.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }


    //마이페이지 이동
    @GetMapping("/user/myPage")
    public String myPage(Principal principal, Optional<Integer> page ,Model model) {

        Pageable pageable = PageRequest.of(page.orElse(0), 10);
        MyPageDto myPageDto = userService.userInfo(principal.getName(), pageable);

        model.addAttribute("myPageDto", myPageDto);

        return "User/myPage";
    }

    // 아이디 찾기 페이지
    @GetMapping("/user/forget")
    public String forgetId(@RequestParam String target, Model model) {
        model.addAttribute("target", target);
        return "User/findIdAndPassword";
    }

    // 로그인 유지
    @PostMapping("/user/signIn")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        try {
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Remember-me 수동 처리 (필요시)
            if (loginRequest.isAutoLogin()) {
                PersistentTokenBasedRememberMeServices rememberMeServices =
                        new PersistentTokenBasedRememberMeServices("a-very-secret-key", userDetailsService, tokenRepository);
                rememberMeServices.setAlwaysRemember(true);
                rememberMeServices.loginSuccess(request, response, auth);
            }

            return ResponseEntity.ok().body("로그인 성공");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }


    //로그인 페이지
    @GetMapping("/user/signIn")
    public String signIn(){
        return "User/signIn";
    }


    //회원가입 페이지 제공
    @GetMapping("/user/signUp")
    public String signUp(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "User/signUp";
    }

    //회원가입 처리
    @PostMapping("/user/signUp")
    public String signUp(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){
//        if(bindingResult.hasErrors()){
//            return "User/signUp";
//        }
        userService.signUpSave(memberFormDto,passwordEncoder);
        return "redirect:/";
    }

    // 회원가입시 아이디 중복체크
    @PostMapping("/user/idCheck")
    public @ResponseBody ResponseEntity idCheck(String id){

        boolean isDup =  userService.idCheck(id);
        if(isDup) {
            return new ResponseEntity<String>("사용불가", HttpStatus.BAD_REQUEST);
        }else {
            return new ResponseEntity<String>("사용가능", HttpStatus.OK);
        }
    }

}
