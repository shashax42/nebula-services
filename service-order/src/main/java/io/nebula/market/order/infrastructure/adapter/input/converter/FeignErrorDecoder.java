package io.nebula.market.order.infrastructure.adapter.input.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() < 500) {
            try (InputStream bodyIs = response.body().asInputStream()) {
                ProblemDetail problemDetail = objectMapper.readValue(bodyIs, ProblemDetail.class);
                return new ResponseStatusException(
                        HttpStatus.valueOf(problemDetail.getStatus()),
                        problemDetail.getDetail()
                );
            } catch (IOException e) {
                return new Exception("Failed to parse error response", e);
            }
        }
        return new Exception(response.reason());
    }
}
