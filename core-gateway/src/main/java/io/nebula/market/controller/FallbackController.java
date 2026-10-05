package io.nebula.market.controller;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    final String FALLBACK_MSG = "Circuitbreaker 활성됨. 조금만 기다려주세요... \uD83D\uDE2D";

    @GetMapping("/account")
    public ResponseEntity<ProblemDetail> fallbackAccount() {
        return ResponseEntity.status(SERVICE_UNAVAILABLE)
                .body(problemDetail("account"));
    }

    @GetMapping("/order")
    public ResponseEntity<ProblemDetail> fallbackOrder() {
        return ResponseEntity.status(SERVICE_UNAVAILABLE)
                .body(problemDetail("order"));
    }

    @GetMapping("/product")
    public ResponseEntity<ProblemDetail> fallbackProduct() {
        return ResponseEntity.status(SERVICE_UNAVAILABLE)
                .body(problemDetail("product"));
    }

    private ProblemDetail problemDetail(String path) {
        var problemdetail = ProblemDetail.forStatus(SERVICE_UNAVAILABLE);
        problemdetail.setType(URI.create("/errors/gateway/fallback"));
        problemdetail.setTitle("서비스 이용 불가");
        problemdetail.setDetail(FALLBACK_MSG);
        problemdetail.setInstance(URI.create(path));

        return problemdetail;
    }
}