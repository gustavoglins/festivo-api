package com.festivo.domain.services.impl;

import com.festivo.domain.dto.ResetPasswordEmailDTO;
import com.festivo.domain.services.interfaces.EmailService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${api.email}")
    private String apiEmail;

    private Resend resend;

    @PostConstruct
    public void init() {
        this.resend = new Resend(resendApiKey);
    }

    @Override
    public void sendResetPasswordEmail(ResetPasswordEmailDTO resetPasswordEmailDTO) {
        String template = loadTemplate("reset-password.html");
        String userFirstName = resetPasswordEmailDTO.username().trim().split(" ")[0];

        String htmlBody = template
                .replace("{{userName}}", userFirstName)
                .replace("{{redirectLink}}", "http://localhost:4200/auth/reset-password?token=" + resetPasswordEmailDTO.token());

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(apiEmail)
                .to(resetPasswordEmailDTO.to())
                .subject("Reset Password")
                .html(htmlBody)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            log.info(data.toString());
        } catch (ResendException exception) {
            throw new RuntimeException(exception.getMessage(), exception.getCause());
        }
    }

    private String loadTemplate(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("email-templates/" + fileName)) {
            if (inputStream == null) throw new RuntimeException("Template not found: '" + fileName + "'");
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage(), exception.getCause());
        }
    }
}
