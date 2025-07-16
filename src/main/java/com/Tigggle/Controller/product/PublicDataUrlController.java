package com.Tigggle.Controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class PublicDataUrlController {

    // 👉 디코딩된(원본) 서비스 키를 그대로 사용해야 함
    private final String serviceKey = "s7XXMDNhYvKn5GaicPEQKaDHCXkkyUTbT6dDBDMkY/FUZshurWeKIB7kuqVAg9t3X8PlSJxSddWLr6hfPz5hog==";

    @GetMapping(value = "/deposit-api-url", produces = "application/xml")
    public ResponseEntity<String> getDepositApi(
            @RequestParam String sBseDt,
            @RequestParam String eBseDt,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows) {

        try {
            String apiUrl = "https://apis.data.go.kr/B190030/GetDepositProductInfoService/getDepositProductList"
                    + "?serviceKey=" + serviceKey
                    + "&pageNo=" + pageNo
                    + "&numOfRows=" + numOfRows
                    + "&sBseDt=" + sBseDt
                    + "&eBseDt=" + eBseDt;

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            return ResponseEntity
                    .ok()
                    .header("Content-Type", "application/xml; charset=UTF-8")
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("API 호출 실패: " + e.getMessage());
        }
    }
}
