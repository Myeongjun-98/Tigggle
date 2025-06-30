package com.Tigggle.Controller.Community;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class CommunityController {

    @GetMapping("community/tip")

        public String communityTip() {

        return "communityTip";

    }

}
