package io.nebula.market.account.infrastructure.adapter.output.messaging.producer;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
public class ProblemDetailCreator {
    public ProblemDetail create(String title, Exception ex, HttpStatus status, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(request.getRequestURL().toString()));
        problemDetail.setTitle(title);
        problemDetail.setDetail(ex.getLocalizedMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }
}