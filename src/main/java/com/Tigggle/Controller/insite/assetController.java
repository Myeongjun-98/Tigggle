package com.Tigggle.Controller.insite;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class assetController {

    @GetMapping("")
    public String asset(){
        return "";
    }
}
