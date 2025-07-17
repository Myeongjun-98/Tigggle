package com.Tigggle.Controller.product;

import com.Tigggle.Constant.FinancialProduct.DataSource;
import com.Tigggle.Constant.FinancialProduct.ProductType;
import com.Tigggle.Entity.product.Bank;
import com.Tigggle.Entity.product.Product;
import com.Tigggle.Repository.product.BankRepository;
import com.Tigggle.Repository.product.ProductRepository;
import com.Tigggle.Service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor

public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final BankRepository bankRepository;

    @GetMapping("/product")
    public String showProductPage(Model model) {
        List<Product> products = productRepository.findAll();
        List<Bank> allBanks = bankRepository.findAll();

        List<Bank> banksWithProducts = allBanks.stream()
                .filter(bank -> bank.getProducts() != null && !bank.getProducts().isEmpty())
                .collect(Collectors.toList());

        model.addAttribute("products", products);
        model.addAttribute("banks", banksWithProducts);
        return "product/Product";
    }

    // 상품 수기 등록
    @PostMapping("/admin/product/manual-register")
    public String registerManualProduct(@RequestParam String bankName,
                                        @RequestParam String logoUrl,
                                        @RequestParam String homepageUrl,
                                        @RequestParam String productName,
                                        @RequestParam ProductType productType,
                                        @RequestParam int periodMonth,
                                        @RequestParam Long amountMoney,
                                        @RequestParam float interestRate,
                                        RedirectAttributes redirectAttributes) {
        productService.registerManualProduct(
                bankName, logoUrl, homepageUrl,
                productName, productType, periodMonth, amountMoney, interestRate
        );
        redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 등록되었습니다.");
        return "redirect:/product";
    }


    // 등록 페이지 이동
    @GetMapping("/admin/product/register")
    public String showRegisterPage() {

        return "product/ProductRegister";
    }

    // 수동으로 등록한 상품 삭제
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/product/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // 로그 출력하는게 좋음
            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
        }
    }

}
