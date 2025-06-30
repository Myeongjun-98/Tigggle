package com.Tigggle.Controller.insite;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class instieController {

    @GetMapping("/")
    public String insite(){
        return "/";
    }

    // 엑셀로 다운로드
    public String
}
