package com.Tigggle.Controller.insite;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

public class assetController {

        @GetMapping("/aaset")
        public String asset(){
            return "insite/asset";
        }
}

