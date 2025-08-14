# STEAM-PUNK 프로젝트 상세 분석

## 1. 프로젝트 개요

**STEAM-PUNK**는 가상의 콘솔 게임 거래 플랫폼을 위한 백엔드 API 서버입니다. MSA(마이크로서비스 아키텍처)를 기반으로 설계되어, 대규모 트래픽이 발생하는 한정 수량 할인 판매 이벤트 등에 유연하게 대응할 수 있습니다.

이 문서는 코드 레벨에서 분석한 프로젝트의 아키텍처, 모듈별 기능, 그리고 주요 기술 패턴에 대한 상세한 설명을 제공합니다.

## 2. 기술 스택

- **언어**: Java 21
- **프레임워크**: Spring Boot 3.2.5, Spring Cloud 2023.0.1
- **빌드 도구**: Gradle (Multi-module Project)
- **데이터베이스**: MySQL (운영), H2 (테스트)
- **핵심 라이브러리**:
    - Spring Data JPA: 데이터 영속성 관리
    - Spring Security: 인증 및 인가
    - Spring WebFlux (`WebClient`): 마이크로서비스 간 동기 HTTP 통신
    - JJWT: JSON Web Token 생성 및 검증
    - Spring ApplicationEventPublisher: 서비스 내 이벤트 기반 처리
    - Lombok: 보일러플레이트 코드 감소
    - Mockito & JUnit 5: 단위 및 통합 테스트

## 3. 아키텍처 및 모듈별 기능

이 프로젝트는 Spring Cloud를 기반으로 한 전형적인 마이크로서비스 아키텍처를 따릅니다.

- **`steam-punk-service-registry`**: Netflix Eureka를 사용하여 구현된 서비스 레지스트리입니다. 각 마이크로서비스가 자신의 위치를 등록하고 다른 서비스를 찾을 수 있도록(Service Discovery) 돕습니다.
- **`steam-punk-api`**: API 게이트웨이 역할을 수행하며, 외부의 모든 요청에 대한 단일 진입점을 제공합니다. 요청을 인증하고 적절한 내부 마이크로서비스로 라우팅하는 책임을 가집니다.

---

### 3.1. 핵심 서비스 모듈

각 모듈은 독립적인 기능을 수행하며, 필요한 경우 `WebClient`를 통해 다른 서비스의 API를 호출합니다.

#### **`steam-punk-user` (유저 서비스)**
- **주요 책임**: 사용자 정보 관리(가입, 조회, 수정), 사용자 포인트 충전 및 차감.
- **특징**:
    - **데이터 암호화**: `email`, `phoneNumber`와 같은 민감한 사용자 정보를 `AES256ToStringConverter`라는 JPA AttributeConverter를 통해 데이터베이스에 암호화하여 저장합니다. 암호화 키는 `spring.cryptography.aes.secret-key` 프로퍼티를 통해 주입됩니다.
    - **도메인 로직**: `User` 도메인 객체 내에 포인트 차감/증가 시의 유효성 검증 로직(`NOT_ENOUGH_POINT` 등)이 포함되어 있습니다.

#### **`steam-punk-auth` (인증 서비스)**
- **주요 책임**: 사용자 로그인 처리 및 JWT(Access/Refresh Token) 발급.
- **특징**:
    - **외부 서비스 호출**: 로그인을 위해 `WebClient`를 사용하여 `steam-punk-user` 서비스로부터 사용자 정보를 조회합니다.
    - **JWT 기반 인증**: `JwtProvider` 컴포넌트가 `spring.jwt.*` 프로퍼티를 기반으로 토큰을 생성합니다.
    - **상태 비저장**: 인증 상태를 세션에 저장하지 않고, 발급된 JWT를 통해 각 요청을 인증합니다.

#### **`steam-punk-product` (상품 서비스)**
- **주요 책임**: 게임 상품 정보(목록 조회, 상세 조회, 등록) 관리.
- **특징**:
    - **외부 서비스 호출**: 한정 수량 상품의 경우, `WebClient`를 통해 `steam-punk-product-stock` 서비스와 통신하여 재고를 확인하거나 차감합니다.
    - **복합적인 도메인**: `Product`, `Category`, `ProductDiscountPolicy` 등 여러 도메인 객체가 관계를 맺어 상품 정보를 표현합니다.

#### **`steam-punk-payment` (결제 서비스)**
- **주요 책임**: 결제 프로세스 오케스트레이션(결제 생성, 실행, 환불 처리).
- **특징**:
    - **다중 서비스 호출**: 하나의 결제 프로세스를 위해 `order`, `user`, `game` 등 여러 서비스의 API를 `WebClient`로 호출하여 필요한 정보를 가져오고 상태를 검증합니다.
    - **이벤트 기반 처리**: 결제가 성공하거나 실패했을 때 `ApplicationEventPublisher`를 통해 각각 `PaymentCompletedEvent`, `PaymentFailedEvent`를 발행합니다. 이를 통해 결제와 후속 처리(예: 주문 상태 변경, 재고 복구)의 결합도를 낮춥니다.
    - **전략 패턴**: `RefundPolicy` 인터페이스와 그 구현체인 `RegularRefundPolicy`를 통해 환불 정책 로직을 분리하고 확장 가능하게 설계했습니다.

## 4. 테스트 전략 분석

이 프로젝트의 테스트는 각 모듈의 `src/test` 디렉터리에 작성되었습니다.

- **도메인 테스트**: 순수 비즈니스 로직을 검증하는 단위 테스트입니다. 외부 의존성이 없으며, `new` 키워드를 통해 객체를 생성하고 로직의 결과를 검증합니다.
- **서비스 테스트**: 서비스 레이어의 로직을 검증하는 테스트입니다.
    - **의존성 Mocking**: `Mockito`를 사용하여 Repository, `PasswordEncoder` 등 서비스의 내부 의존 객체를 Mock(가짜 객체)으로 만듭니다.
    - **외부 API 호출 처리**: `WebClient`를 사용하는 정적(static) 팩토리 메서드(`WebClient.create()`, `OrderInfo.from...()`) 호출을 처리하기 위해 `Mockito.mockStatic()` 기능을 사용합니다. 이를 통해 외부 서비스 호출 결과를 테스트 케이스 내에서 제어할 수 있습니다.
- **테스트 환경**:
    - 각 모듈은 `build.gradle`에 `h2` 데이터베이스 의존성을 `testImplementation`으로 포함하여, 테스트 실행 시 내장 인메모리 DB를 사용합니다.
    - `src/test/resources/application.properties` 파일을 통해 암호화 키, JWT 시크릿 키 등 테스트에 필요한 프로퍼티를 제공하여 전체 애플리케이션 컨텍스트가 성공적으로 로드될 수 있도록 구성합니다.
