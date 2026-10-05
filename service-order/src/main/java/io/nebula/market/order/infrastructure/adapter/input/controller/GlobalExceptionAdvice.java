package io.nebula.market.order.infrastructure.adapter.input.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import io.nebula.market.order.domain.exception.BizException;
import io.nebula.market.order.infrastructure.adapter.output.messaging.producer.ProblemDetailCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.format.DateTimeParseException;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {
    private static final String BIZ_EXCEPTION_TITLE = "비즈니스 예외 처리";

    private final ProblemDetailCreator problemDetailCreator;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BizException.class)
    public ProblemDetail handleBizException(
            BizException ex,
            HttpServletRequest request
    ) {
        return problemDetailCreator.create(BIZ_EXCEPTION_TITLE, ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ProblemDetail handleDateTimeParseException(
            DateTimeParseException ex,
            HttpServletRequest request
    ) {
        return problemDetailCreator.create(BIZ_EXCEPTION_TITLE, ex, HttpStatus.BAD_REQUEST, request);
    }
}
