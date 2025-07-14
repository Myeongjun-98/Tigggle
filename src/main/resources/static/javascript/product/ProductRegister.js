// JS에서 국내 주요 은행 자동입력 기능 (선택 사항)
 const bankInfoMap = {
     "한국산업은행": {
         logoUrl: "/images/KDB_logo.png",
         homepageUrl: "https://www.kdb.co.kr"
     },
     "국민은행": {
         logoUrl: "/images/kb_logo.png",
         homepageUrl: "https://www.kbstar.com"
     },
     "우리은행": {
         logoUrl: "/images/woori_logo.png",
         homepageUrl: "https://www.wooribank.com"
       },
       "하나은행": {
         logoUrl: "/images/hana_logo.png",
         homepageUrl: "https://www.kebhana.com"
       },
       "농협은행": {
         logoUrl: "/images/nh_logo.png",
         homepageUrl: "https://www.nonghyup.com"
       },
       "SC제일은행": {
         logoUrl: "/images/sc_logo.png",
         homepageUrl: "https://www.standardchartered.co.kr"
       },
       "한국씨티은행": {
         logoUrl: "/images/citi_logo.png",
         homepageUrl: "https://www.citibank.co.kr"
       },
       "카카오뱅크": {
         logoUrl: "/images/kakaobank_logo.jpg",
         homepageUrl: "https://www.kakaobank.com"
       },
       "토스뱅크": {
         logoUrl: "/images/tossbank_logo.png",
         homepageUrl: "https://www.tossbank.com"
       },
       "케이뱅크": {
         logoUrl: "/images/kbank_logo.png",
         homepageUrl: "https://www.kbanknow.com"
       }
 };

 document.addEventListener('DOMContentLoaded', function () {
     const bankNameInput = document.getElementById('bank-name');
     const logoInput = document.getElementById('bank-logo');
     const urlInput = document.getElementById('bank-url');

     if (bankNameInput && logoInput && urlInput) {
         bankNameInput.addEventListener('input', function () {
             const bank = bankInfoMap[this.value.trim()];
             if (bank) {
                 logoInput.value = bank.logoUrl;
                 urlInput.value = bank.homepageUrl;
             }
         });
     }
 });

function confirmCancel() {
  if (confirm("정말 취소하시겠습니까? 변경 내용이 저장되지 않습니다.")) {
    // 이동할 주소, 상품 페이지 URL로 변경하세요.
    window.location.href = "/product";
  }
}
