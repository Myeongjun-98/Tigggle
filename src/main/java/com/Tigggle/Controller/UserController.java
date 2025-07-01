package com.Tigggle.Controller;

import com.Tigggle.DTO.MemberFormDto;
import com.Tigggle.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/user/signIn")
    public String signIn(){
        return "User/signIn";
    }
    @GetMapping("/user/signUp")
    public String signUp(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "User/signUp";
    }
    @PostMapping("/user/signUp")
    public String signUp(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){
//        if(bindingResult.hasErrors()){
//            return "User/signUp";
//        }
        userService.signUpSave(memberFormDto,passwordEncoder);
        return "redirect:/";
    }

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
