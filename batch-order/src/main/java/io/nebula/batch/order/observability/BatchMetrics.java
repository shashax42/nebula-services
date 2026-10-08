package io.nebula.batch.order.observability;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

/**
 * 배치 실행 결과 지표 (Push Gateway 가 하던 "마지막 실행" 값을 OTLP 로 대신 보낸다).
 *
 * <pre>
 * nebula.batch.job.last.run{job.name, status}   마지막으로 끝난 시각 (epoch seconds)
 * nebula.batch.job.duration{job.name, status}   실행 시간 (seconds)
 * nebula.batch.order.rows{job.name}             처리(취소·삭제)한 주문 수
 * </pre>
 *
 * <p>프로세스가 매번 새로 뜨므로 카운터 대신 게이지를 쓴다. 카운터는 매 실행 0 에서 시작해 증가량을 계산할 수 없다.
 * 실행마다 바뀌는 파드 이름은 collector 가 라벨로 붙이지 않으므로 같은 job.name 의 값이 한 시리즈로 이어진다.
 */
@Component
public class BatchMetrics {

    private final MeterRegistry registry;
    private final Map<String, AtomicReference<Double>> gauges = new ConcurrentHashMap<>();

    public BatchMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void recordRun(String jobName, boolean success, Duration duration, long rows) {
        Tags tags = Tags.of("job.name", jobName, "status", success ? "completed" : "failed");
        set("nebula.batch.job.last.run", "seconds", "Epoch seconds when the job last finished", tags,
                Instant.now().getEpochSecond());
        set("nebula.batch.job.duration", "seconds", "Job execution time", tags, duration.toMillis() / 1000.0);
        if (success) {
            set("nebula.batch.order.rows", null, "Orders updated or deleted by the last successful run",
                    Tags.of("job.name", jobName), rows);
        }
    }

    private void set(String name, String unit, String description, Tags tags, double value) {
        String key = name + tags;
        gauges.computeIfAbsent(key, k -> {
            AtomicReference<Double> holder = new AtomicReference<>(0.0);
            Gauge.builder(name, holder, AtomicReference::get)
                    .description(description)
                    .baseUnit(unit)
                    .tags(tags)
                    .register(registry);
            return holder;
        }).set(value);
    }
}
