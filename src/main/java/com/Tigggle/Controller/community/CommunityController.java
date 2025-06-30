package com.Tigggle.Controller.community;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class CommunityController {

    @GetMapping("/communityTip")

        public String communityTip() {

        return "community/Tip";

    }

    @GetMapping("/communityDiscussion")

        public String communityDiscussion() {

            return "community/Discussion";

    }

    @GetMapping("/communityEconomicMarket")

    public String communityEconomicMarket() {

        return "community/EconomicMarket";
    }

}
