# TripHub 프로젝트 개발 계획

## 1. 프로젝트 개요
TripHub은 여행 및 숙박 예약 플랫폼으로, MSA 기반으로 구성되며 CI/CD, 클라우드 배포, 테스트 자동화까지 고려한 실전형 사이드 프로젝트입니다. 프로젝트는 Monorepo 구조로 관리되며, 각 기능은 마이크로서비스로 분리되어 운영됩니다.

---

## 2. 전체 개발 흐름

1. **핵심 기능(MVP) 구현**
   - 예: 사용자 회원가입, 로그인
   - Spring Boot 기반으로 단일 서비스 먼저 구성
   - React 프론트엔드에서 호출 가능하도록 간단한 UI 연동

2. **Monorepo 구조 세팅**
   - 백엔드, 프론트엔드, 공통 모듈 분리
   - 예: `apps/backend-auth`, `apps/frontend`, `libs/common`

3. **CI/CD 구축 (GitHub Actions)**
   - PR 생성 시 빌드/테스트 자동화
   - main 브랜치 병합 시 자동 빌드 및 배포 준비

4. **인프라 구성 (AWS)**
   - EC2, ECS, 혹은 EKS 기반의 서비스 배포 환경 구성
   - RDS(MySQL/PostgreSQL) 사용 예정
   - Kafka 클러스터 구성 (비동기 메시징 처리용)
   - 추후 도메인 연결, HTTPS, 로드 밸런싱 고려

5. **아키텍처 구성도 작성**
   - 실제 구현된 구조 반영하여 시각화
   - 서비스 간 통신, 데이터 흐름, 인프라 요소 포함

6. **도메인 확장 및 운영 기능 추가**
   - 예약, 결제, 리뷰, 사용자 관리 등 마이크로서비스 분리 및 확장
   - Kafka 기반 이벤트 처리 도입 (예: 결제 완료 후 예약 상태 변경)
   - 로깅, 모니터링, 장애 대응 체계 추가

---

## 3. 기술 스택

### 백엔드
- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Cloud (MSA 구성)
- Spring Data JPA
- MyBatis (일부 모듈)
- PostgreSQL or MySQL
- Redis (세션/캐시)
- Apache Kafka (이벤트 기반 메시징)
- Gradle (빌드 도구)
- gRPC (서비스 간 통신)

### 프론트엔드
- React 18+
- TypeScript
- Vite (번들러)
- Tailwind CSS
- Zustand or Recoil (상태 관리)

### 인프라 & DevOps
- AWS (EC2, S3, RDS, Route 53, CloudFront 등)
- GitHub Actions (CI/CD)
- Docker
- Docker Compose (로컬 개발)
- Terraform (선택적 인프라 코드화)
- Nginx (리버스 프록시)
- Apache Kafka (MSA 간 비동기 메시징)

### 테스트 & 품질 관리
- JUnit 5
- Mockito
- Testcontainers (통합 테스트)
- ESLint + Prettier (프론트엔드 코드 스타일)
- GitHub Actions 테스트 자동화

### 프로젝트 구조
- Monorepo (백엔드 + 프론트엔드 통합 관리)
- apps/, libs/, docs/ 디렉토리 기준 분리

---

## 4. 이런 순서로 진행하는 이유

| 단계 | 이유 |
|------|------|
| 1. 기능 구현 | 실제 동작하는 코드를 기준으로 설계/배포를 결정할 수 있음 |
| 2. CI/CD 구축 | 반복적인 빌드/테스트 자동화로 생산성 향상 |
| 3. 인프라 구성 | 검증된 코드를 안정적으로 배포 가능 |
| 4. 구성도 작성 | 현실에 맞는 설계 반영 가능 |

회사에서도 기능 → 자동화 → 배포 → 설계 순으로 진행하며, 이상적인 플랜이 아닌 **현실에서 가능한 방향**으로 점진적으로 개발합니다.