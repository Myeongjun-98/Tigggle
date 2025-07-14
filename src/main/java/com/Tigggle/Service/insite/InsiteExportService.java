package com.Tigggle.Service.insite;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

@Service
@RequiredArgsConstructor
public class InsiteExportService {

    private final TemplateEngine templateEngine;

    public byte[] generatePdf(ModelMap modelData) throws Exception {
        // 1. HTML 렌더링
        Context context = new Context();
        context.setVariables(modelData);
        String html = templateEngine.process("insite/insite-pdf", context);  // 별도의 PDF용 html 템플릿 사용 권장

        // 2. HTML → PDF 변환
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();

            // ✅ 폰트 등록
            builder.useFont(
                    () -> getClass().getResourceAsStream("/static/fonts/NotoSansKR-Regular.ttf"),
                    "Noto Sans KR"
            );

            // ✅ HTML 렌더링 설정
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();


        }
    }

}
