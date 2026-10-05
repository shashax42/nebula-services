# nebula-services

Nebula-Platform 위에서 동작하는 Spring Cloud 기반 마이크로서비스 모노레포.

| 서비스 | 설명 |
|---|---|
| `core-discovery` | Eureka 서비스 디스커버리 |
| `core-gateway` | API 게이트웨이 |
| `service-account` | 계정 서비스 |
| `service-product` | 상품 서비스 |
| `service-order` | 주문 서비스 (Kafka purchase/refund 이벤트) |
| `batch-order` | 주문 배치 (CronJob) |

- 공통: Java 21, Spring Boot 3.3, Gradle
- 로컬 의존성: `tools/docker-compose-db.yml`, `tools/docker-compose-kafka.yml`
- 배포 매니페스트: `nebula-gitops` 레포
