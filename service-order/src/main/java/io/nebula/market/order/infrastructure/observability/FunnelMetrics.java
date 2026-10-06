package io.nebula.market.order.infrastructure.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 주문 사가(Transactional Event Flow) 단계별 카운터.
 *
 * <p>메트릭 {@code nebula.commerce.funnel.events{funnel.stage, reason}} 은 OTLP 로 OTel collector 에 전송되고
 * Nebula-Monitoring 의 레코딩 규칙이 단계별 전환율·보상(취소) 비율·적체를 계산한다.
 *
 * <pre>
 * order_placed ─▶ purchase_published ─▶ purchase_consumed ─┬─▶ (재고 차감 성공)
 *   (service-order)  (service-order)      (service-product)  └─▶ stock_rejected ─▶ order_canceled
 *                                                              (service-product)   (service-order)
 * </pre>
 *
 * <p>reason 은 카디널리티를 막기 위해 고정된 값(out_of_stock, publish_error, none)만 쓴다.
 */
@Component
@RequiredArgsConstructor
public class FunnelMetrics {

    public static final String ORDER_PLACED = "order_placed";
    public static final String PURCHASE_PUBLISHED = "purchase_published";
    public static final String PURCHASE_PUBLISH_FAILED = "purchase_publish_failed";
    public static final String ORDER_CANCELED = "order_canceled";

    private final MeterRegistry registry;

    public void record(String stage) {
        record(stage, "none");
    }

    public void record(String stage, String reason) {
        Counter.builder("nebula.commerce.funnel.events")
                .description("Order saga events by stage")
                .tag("funnel.stage", stage)
                .tag("reason", reason)
                .register(registry)
                .increment();
    }

    /** 자유 텍스트 사유(예: "재고 부족>3")를 고정 값으로 정규화한다. */
    public static String normalizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "none";
        }
        return reason.startsWith("재고 부족") ? "out_of_stock" : "other";
    }
}
