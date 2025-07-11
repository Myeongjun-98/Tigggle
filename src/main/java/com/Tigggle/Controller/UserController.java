package com.Tigggle.Controller;

import com.Tigggle.DTO.MemberFormDto;
import com.Tigggle.DTO.MyPageDto;
import com.Tigggle.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


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
