package com.Tigggle.Controller.insite;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class instieController {

    @GetMapping("/insite")
    public String insite(){
        return "/insite";
    }

    // 월간 소비내역
    //public String
    // 이번달 소비내역

    // 엑셀로 다운로드


}
