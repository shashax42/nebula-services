# nebula-services

Nebula-Platform 위에서 동작하는 Spring Cloud 기반 마이크로서비스 모노레포.

| 서비스 | 설명 |
|---|---|
| `core-discovery` | Eureka 서비스 디스커버리 — **로컬 개발(`dev` 프로필) 전용**, CI·클러스터 배포 대상 아님 |
| `core-gateway` | API 게이트웨이 |
| `service-account` | 계정 서비스 |
| `service-product` | 상품 서비스 |
| `service-order` | 주문 서비스 (Kafka purchase/refund 이벤트) |
| `batch-order` | 주문 배치 (CronJob) |

- 공통: Java 21, Spring Boot 3.3, Gradle
- 로컬 의존성: `tools/docker-compose-db.yml`, `tools/docker-compose-kafka.yml`
- 배포 매니페스트: `nebula-gitops` 레포

## 서비스 디스커버리

쿠버네티스(`kubernetes` 프로필)에서는 Eureka를 끄고 Spring Cloud Kubernetes로
Kubernetes Service를 직접 조회한다. 그래서 `core-discovery`는 클러스터에 배포하지 않고
CI 파이프라인도 두지 않는다. 로컬 환경을 kind로 통일한 뒤 제거할 예정이다.
