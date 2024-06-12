## 가상 게임 거래 플랫폼 백엔드 API

- 게임 개발사와 사용자가 **콘솔게임**을 거래하는 가상 게임 플랫폼 웹 애플리케이션 백엔드API 입니다.
- **게임 한정 수량 할인 판매 이벤트**와 같은 대량 트래픽 상황에 스케일 아웃이 용이하도록 **MSA 아키텍처**로 설계했습니다.
- Kaggle의 최신 스팀 게임 데이터셋(약 86,000건)을 활용해서 API 테스트를 진행했습니다.
  - https://www.kaggle.com/datasets/fronkongames/steam-games-dataset

## Table Of Contents
- [유저스토리](#유저스토리)
- [주요기능 및 API 문서](#주요기능-및-api-문서)
- [아키텍처](#아키텍처)
- [ERD](#erd)
- [핵심문제 해결과정 및 전략](#핵심문제-해결과정-및-전략)

## 유저스토리
- 유저는 **회원 가입**을 통해 본 서비스의 주 기능을 이용할 수 있습니다.
- **게임 검색 서비스**를 통해 게임을 검색할 수 있습니다.
- **위시리스트**에 상품을 등록 후 **주문** 그리고 **결제**를 통해 게임을 구매합니다.
- 한정 판매 또는 **한정 세일 판매 이벤트** 게임을 선착순으로 구매할 수 있습니다.
- 게임 플레이를 통해 게임을 플레이 할 수 있으며, 이력을 조회할 수 있습니다.

## 주요기능 및 api 문서
- [API 문서(Notion) - Click!](https://www.notion.so/API-a44f03836b054ba680ee623a725329ae?pvs=4)

| 마이크로서비스 | 기능 |
| --- | --- |
| API 게이트웨이 마이크로서비스 | API 단일 진입점, 라우팅 기능 |
| 레지스트리 마이크로서비스 | 서비스 디스커버리 기능 + 로드밸런싱 |
| 유저 마이크로서비스 | 회원 가입, 개인정보 업데이트, 유저 포인트 충전, 차감, 조회 API |
| 인증 마이크로서비스 | 로그인 API |
| 상품 마이크로서비스 | 게임 조회, 게임 목록, 게임 등록 여부 조회, 게임 등록 API (한정수량, 세일 이벤트 상태 반영) |
| 주문 마이크로서비스 | 주문 등록, 조회, 상태 변경, 주문 상품 삭제, 위시리스트 등록, 조회, 삭제 API |
| 결제 마이크로서비스 | 결제 진입, 결제, 유저 결제 목록 조회, 환불, 환불상태 조회 API |
| 상품재고 관리 마이크로서비스 | 한정 판매 상품 등록, 재고 조회, 차감, 증가 API |
| 게임 마이크로서비스 | 유저 게임 추가, 유저 게임이력 조회, 게임 플레이 API |
| 상품 배치 마이크로서비스 | Kaggle 스팀 게임 데이터 전처리 및 DB INSERT 배치 작업(일회성 배치) |

## 아키텍처
![steam-punk-arch](https://github.com/soonhankwon/steam-punk-be/assets/113872320/d039e644-f0ef-4730-ae50-0cfbccf8602f")

## erd
<details>
<summary><strong> ERD - Click! </strong></summary>
<div markdown="1">
  
![erd](https://github.com/soonhankwon/steam-punk-be/assets/113872320/2a02c8da-625b-4055-9841-9dd519e81a54)
</div>
</details>

## 핵심문제 해결과정 및 전략

### 한정수량 판매 상품 재고수량 동시성 제어 문제
- 문제상황: 재고수량이 10개인 **한정세일게임** 판매시 **10,000명의 유저가 동시에 접근**하면 수량 데이터 정합성이 깨지는 문제(레이스 컨디션)가 발생했습니다.
- 해결과정1: 비관적락을 통해 DB Lock을 설정해서 1차적 해결
    - 추가적인 문제점: 10,000명의 유저가 **해당 마이크로서비스에서 트랜잭션 시작 및 DB 커넥션을 점유**하는 문제 발생
    - 아이디어: 10명의 유저만 트랜잭션 시작 및 DB Lock을 점유하게 하는 것이 효율적이지 않을까?
- 해결과정2: Redisson 분산락(글로벌락)을 통해 1차적으로 10명의 유저만 재고수량 서비스 트랜잭션을 시작할 수 있도록 개선 → 이후 10명의 요청에 DB Lock을 적용시켜 데이터 정합성 유지
    
    ```java
    public ProductStockGetResponse decreaseProductStock(Long productId) {
            // Redisson 분산락 적용(Pub,Sub)
            RLock lock = redissonClient.getLock(String.valueOf(productId));
            ProductStock productStock;
            try {
                // waitTime: 락을 기다리는 시간, leaseTime: 락 임대 시간
                lock.tryLock(5, 3, TimeUnit.SECONDS);
                // Lock을 획득한 요청만 트랜잭션 시작
                productStock = stockTransactionService.decreaseByTransaction(productId);
            } catch (InterruptedException e) {
                throw new ApiException(ErrorCode.NO_STOCK_BY_PRODUCT_ID);
            } finally {
                lock.unlock();
            }
            return ProductStockGetResponse.from(productStock);
        }
    ```

- 테스트 및 검증(Apache Jmeter)
![결제진입-테스트-v2(분산락적용)](https://github.com/soonhankwon/steam-punk-be/assets/113872320/12dbbd27-d36a-461f-ab70-938e95fd8f30)
![결제진입-테스트-v2-db](https://github.com/soonhankwon/steam-punk-be/assets/113872320/e70421a8-a0c5-4921-8474-b1d85b39784d)

---
### 상품결제 10,000건 동시처리시 최대 지연시간 0.5초 초과 문제 → 주요쿼리 커버링 인덱스 + 결제서버 스케일 아웃를 통해 0.363초로 개선 (개선율 36.43%)
