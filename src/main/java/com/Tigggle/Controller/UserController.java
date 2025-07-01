package com.Tigggle.Controller;

import com.Tigggle.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("signIn")
    public String signIn(){

        return "User/signIn";
    }

}
