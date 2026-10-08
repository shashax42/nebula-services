package io.nebula.batch.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchOrderApplication {

    public static void main(String[] args) {
        // 잡이 끝나면 컨텍스트를 정상 종료한다 (OTLP 지표·트레이스 마지막 전송).
        // 종료 코드는 Spring Boot 의 JobExecutionExitCodeGenerator 가 정한다: 성공 0, 실패 0 이 아닌 값
        // → CronJob 이 실패를 실패로 기록하고 KubeJobFailed 알림이 동작한다.
        System.exit(SpringApplication.exit(SpringApplication.run(BatchOrderApplication.class, args)));
    }

}
