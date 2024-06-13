## STEAM-PUNK 가상 게임 거래 플랫폼 백엔드 API

- 게임 개발사와 사용자가 **콘솔게임**을 거래하는 가상 게임 플랫폼 웹 애플리케이션 백엔드API 입니다.
- **게임 한정 수량 할인 판매 이벤트**와 같은 대량 트래픽 상황에 스케일 아웃이 용이하도록 **MSA 아키텍처**로 설계했습니다.
- Kaggle의 최신 스팀 게임 데이터셋(약 85,000건)을 활용해서 API 테스트를 진행했습니다.
  - https://www.kaggle.com/datasets/fronkongames/steam-games-dataset

<details>
<summary><strong> 데이터 전처리후 게임 데이터셋 이미지 - Click! </strong></summary>
<div markdown="1">
  
![상품목록조회-Result](https://github.com/soonhankwon/steam-punk-be/assets/113872320/60b435ab-9f57-4777-93be-38e2711594ac)
</div>
</details>

## Table Of Contents
- [유저스토리](#유저스토리)
- [기술스택](#기술스택)
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

## 기술스택

|  | Tech | Usage |
| --- | --- | --- |
| Language | Java 21 |  |
| Framework | SpringBoot 3.2.5 | 스프링부트 WAS |
| Build | Gradle | 빌드툴 |
| Database | MySQL | 관계형 데이터베이스 |
| ORM | Spring Data JPA |  |
| Batch | Spring Batch | Kaggle 스팀 게임 데이터 전처리 및 DB INSERT 배치 작업을 위해 사용 |
| Cloud | Spring Cloud | Spring Eureka, API Gateway |
| Metric | Spring Actuator | 애플리케이션 지표 수집 |
| Libary | WebFlux | 마이크로서비스 간 통신을 위해 사용 |
| Library | Redisson | 재고관리 서비스에서 분산락으로 활용 |
| Library | Jmeter | 성능 및 부하 테스트 툴 |
| Monitoring | Prometheus, Grafana | 애플리케이션 지표 시각화 및 모니터링 |
| DevOps | Docker | WAS 및 마이크로서비스에 필요한 의존성 컨테이너 구동 |
| VCS | Git | 버전 관리 |

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

- Jmeter 결제 API 테스트 지표

| VER | AVG | MAX | TPS | Error% | 적용사항 |
| --- | --- | --- | --- | --- | --- |
| 1 | 0.031s | 0.578s | 310.1/sec | 0.00% | X |
| 2 | 0.033s | 0.555s | 291.8/sec | 0.00% | 결제서비스 스케일아웃 1 → 2 |
| 3 | 0.029s | 0.370s | 325.7/sec | 0.00% | 결제ID, 상품ID, 결제상품상태 인덱스 생성(커버링 인덱스) |
- Grafana 마이크로서비스 메트릭 지표

|  | v1 최대지연시간 | v2 최대지연시간 | v3 최대지연시간 | 메서드 | 1차 개선율 | v1 → v3 개선율 |
| --- | --- | --- | --- | --- | --- | --- |
| PAYMENT-SERVICE | 0.571s | 0.548s | 0.363s | PUT | 4.0215% | 36.43% |
| GAME-SERVICE | 0.168s | 0.103s | 0.159s | POST | 38.6905% | 5.36% |
| ORDER-SERVICE | 0.121s | 0.0654s | 0.0606s | GET | 45.9504% | 49.92% |
| PRODUCT-SERVICE | 0.107s | 0.0462s | 0.0375s | GET | 56.6421% | 64.95% |
| USER-SERVICE | 0.098s | 0.098s | 0.0184s | GET | 0% | 81.22% |
| USER-SERVICE | 0.084s | 0.0315s | 0.0143s | PATCH | 62.5% | 82.98% |
| ORDER-SERVICE | 0.075s | 0.0775s | 0.0295s | PATCH | -3.3333% | 60.67% |

- 문제상황: 일반상품(수량이 없는) 결제 10,000건(유저 10,000명) 동시처리시 최대 지연시간이 목표였던 **0.5초를 초과**하는 문제를 모니터링을 통해 발견했습니다.
- 해결과정1: 결제서비스의 **스케일 아웃(Scale-out)**
    - 아이디어: 트래픽을 2개의 결제서비스에 로드밸런싱해주면 부하를 분산하여 유의미한 성능개선을 보일 수 있다고 생각했습니다.
    - 2개의 결제서비스를 각각 다른 포트에 구동시키고, **유레카(Eureke)를 활용**해 **로드밸런싱** 시켜주었습니다.

![유레카-결제서비스-스케일아웃](https://github.com/soonhankwon/steam-punk-be/assets/113872320/eac2f2a2-780b-4458-94e1-fca9680c3210)

- 테스트 결과: **각 마이크로서비스의 지연시간 개선 및 소폭의 개선**이 있었지만, 결제 API의 TPS 는 동일했습니다.
    - 추가적인 문제점: **로컬에서 컴퓨터 1대 테스트 환경 한계**와 **부하의 특성**입니다.
    - **부하 특성**: 부하가 일정하게 유지되었고, 로드밸런싱으로 인해 단일 서버의 부하가 분산되었지만 전체적인 요청 수는 변하지 않았습니다.
 
- 해결과정2: 결제서비스에서 사용된 주요 **쿼리 분석 및 커버링 인덱스** 적용
    - 문제점: 마이크로서비스 설계로 payment_id를 FK로 따로 지정해주지 않았기 때문에 인덱스가 생성되어 있지 않았고, WHERE 절의 컬럼 또한 인덱스가 적용되지 않았습니다.
    - 개선: 결제ID, 상품ID, 결제상품상태 인덱스 생성
    - **결제상품상태**의 경우 카디널리티가 높지 않기 때문에 추가 인덱스 생성 공간에 따른 트레이드 오프에서 고민되는 지점이 있었지만, **목표 Latency**를 최대한 충족하기 위해 적용했습니다.
- 쿼리실행계획 커버링 인덱스 적용 전후
    <img width="752" alt="결제진입-쿼리튜닝전-플랜" src="https://github.com/soonhankwon/steam-punk-be/assets/113872320/91d0f074-684e-4cd7-bd6b-49988d1e9cdd">
    <img width="752" alt="결제진입-쿼리튜닝-인덱스" src="https://github.com/soonhankwon/steam-punk-be/assets/113872320/7b37dc7d-40f3-45c0-8854-e651e78b0dc4">

- 성능 테스트 및 결과 측정
    - 테스트 환경: 결제서비스 스케일아웃(2개) + 인덱스 적용
    - 결과1: 결제 API: TPS **310.1/sec -> 325.7/sec** **5.03%** 개선
    - 결과2: 결제 마이크로서비스 최대지연시간 **0.571sec -> 0.363s**로 **36.43%** 개선 및 목표치 충족
    - 결과3: 연관된 마이크로서비스 최대지연시간 **5.36% ~ 82.98%** 개선 및 목표치 충족

![결제테스트-쿼리튜닝-v3-메트릭](https://github.com/soonhankwon/steam-punk-be/assets/113872320/38bba37b-b1aa-40f8-b78d-be043796d780)

---

### Kaggle 게임 데이터셋 데이터 전처리 및 DB INSERT 배치 작업 성능 개선 → Chunk 기반 Reader, Writer + 멀티스레드 Step 병렬처리로 29.849sec 에서 12.428sec로 개선(개선율 58.36%)

| VER | 배치소요시간 | 이전 버전 대비 개선율 | 적용사항 |
| --- | --- | --- | --- |
| 1 | 29.849ms | 0% | ConcurrentHashMap에 필요 데이터 캐싱 및 사용 |
| 2 | 32.387ms | -8.50% | Chunk 기반 Reader, Writer로 리팩토링 |
| 3 | 29.129ms | 10.06% | ChunkSize = 100으로 조정 |
| 4 | 12.428ms | 57.33% | TaskExecutor 적용 Step 병렬처리 |

- 문제상황: 85,103건의 게임 JSON 데이터를 전처리하고 데이터베이스에 INSERT하는 배치작업
    - 추가적으로 게임-카테고리에 대한 233,132 INSERT 작업 발생
    - Kaggle 데이터셋의 **특이한 Json구조** 때문에 Custom한 Reader 구현 및 사용
    - 내부적으로 HashMap을 활용해서 key=카테고리명, value=PK(카테고리_ID) 를 캐싱하여 약 29.849초까지 개선
    - Chunk 기반 Reader, Writer를 사용하지 않아 병렬처리를 할 수 없는 문제점이 추가적 발생

- 해결방안1: Reader와 Writer를 **Chunk기반으로 리팩토링**
    - 하지만 32.387sec로 성능 개선 실패 문제 발생

        <details>
        <summary><strong> CODE - Click! </strong></summary>
        <div markdown="1">
        
        ```java
                @Slf4j
                @RequiredArgsConstructor
                @Configuration
                public class JsonJobConfig {
                
                    private final EntityManagerFactory entityManagerFactory;
                    private final CategoryRepository categoryRepository;
                    private static final int CHUNK_SIZE = 100;
                    private final Set<String> categorySet = new HashSet<>();
                    private final Map<String, Long> internalCacheStore = new ConcurrentHashMap<>();
                
                    @Bean
                    public Job jsonConvertAndAddDatabaseJob(PlatformTransactionManager transactionManager, JobRepository jobRepository)
                            throws IOException {
                        return new JobBuilder("jsonConvertAndAddDatabaseJob", jobRepository)
                                .start(getDistinctCategoriesStep(transactionManager, jobRepository))
                                .next(addCategoriesStep(transactionManager, jobRepository))
                                .next(jsonConvertAndAddDatabaseJobStep(transactionManager, jobRepository))
                                .build();
                    }
                
                    @Bean
                    @JobScope
                    public Step getDistinctCategoriesStep(PlatformTransactionManager transactionManager,
                                                          JobRepository jobRepository) {
                        return new StepBuilder("getDistinctCategoriesStep", jobRepository)
                                .tasklet(readJsonCollectDistinctCategoriesTasklet(), transactionManager)
                                .build();
                    }
                
                    public Tasklet readJsonCollectDistinctCategoriesTasklet() {
                        return ((contribution, chunkContext) -> {
                            readJsonCollectDistinctCategories();
                            return RepeatStatus.FINISHED;
                        });
                    }
                
                    private void readJsonCollectDistinctCategories() {
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            JsonNode rootNode = objectMapper.readTree(
                                    new File("/Users/soon/Downloads/archive/games.json"));
                
                            Iterator<String> appIds = rootNode.fieldNames();
                
                            while (appIds.hasNext()) {
                                String id = appIds.next();
                                JsonNode gameNode = rootNode.get(id);
                                JsonNode genresJsonNode = gameNode.get("genres");
                
                                if (genresJsonNode != null && !genresJsonNode.isEmpty()) {
                                    Iterator<JsonNode> iterator = genresJsonNode.iterator();
                                    iterator.forEachRemaining(g -> {
                                        String categoryName = g.asText();
                                        categorySet.add(categoryName);
                                    });
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed Batch");
                        }
                    }
                
                    @Bean
                    @JobScope
                    public Step addCategoriesStep(PlatformTransactionManager transactionManager,
                                                  JobRepository jobRepository) {
                        return new StepBuilder("addCategoriesStep", jobRepository)
                                .tasklet(addCategoriesTasklet(), transactionManager)
                                .build();
                    }
                
                    public Tasklet addCategoriesTasklet() {
                        return ((contribution, chunkContext) -> {
                            addCategories();
                            return RepeatStatus.FINISHED;
                        });
                    }
                
                    private void addCategories() {
                        categorySet.iterator().forEachRemaining(c -> {
                            Category category = new Category(c);
                            category = categoryRepository.save(category);
                            internalCacheStore.put(category.getName(), category.getId());
                        });
                    }
                
                    @Bean
                    public Step jsonConvertAndAddDatabaseJobStep(PlatformTransactionManager transactionManager,
                                                                 JobRepository jobRepository)
                            throws IOException {
                        return new StepBuilder("jsonConvertAndAddDatabaseJobStep", jobRepository)
                                .<Product, Product>chunk(CHUNK_SIZE, transactionManager)
                                .reader(jsonProductItemReader())
                                .writer(jpaProductItemWriter())
                                .build();
                    }
                
                    @Bean
                    public ItemReader<Product> jsonProductItemReader() throws IOException {
                        return new JsonProductItemReader(
                                "/Users/soon/Downloads/archive/games.json", internalCacheStore);
                    }
                
                    @Bean
                    public JpaItemWriter<Product> jpaProductItemWriter() {
                        JpaItemWriter<Product> jpaItemWriter = new JpaItemWriter<>();
                        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
                        return jpaItemWriter;
                    }
                }
                
        ```
        </div>
        </details>

- 해결방안2: ChunkSize = 100으로 조정
    - 약 29.129초로 개선
- 해결방안3: Chunk 기반으로 Reader와 Writer를 리팩토링함으로써 **Step을 멀티스레드 병렬처리 적용(TaskExecutor)**
    - 추가적인 문제점: 병렬처리를 위한 Reader 동기화 처리 문제 발생
    - Reader(Custom)에 **synchoronized 키워드**를 추가해서 해결

        <details>
        <summary><strong> CODE - Click! </strong></summary>
        <div markdown="1">
        
        ```java
            public class JsonProductItemReader implements ItemReader<Product> {
        
            private final Iterator<JsonNode> productIterator;
            private final Map<String, Long> internalCacheMap;
        
            public JsonProductItemReader(String filePath, Map<String, Long> cacheMap) throws IOException {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(new File(filePath));
                this.productIterator = rootNode.elements();
                this.internalCacheMap = cacheMap;
            }
        
            @Override
            public synchronized Product read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
                if (productIterator != null && productIterator.hasNext()) {
                    JsonNode node = productIterator.next();
                    String name = node.get("name").asText();
                    double price = node.get("price").asDouble();
                    String shortDescription = node.get("short_description").asText();
                    String headerImage = node.get("header_image").asText();
                    String webSite = node.get("website").asText();
                    JsonNode developersJsonNode = node.get("developers");
                    String developer;
                    if (developersJsonNode == null || developersJsonNode.isEmpty()) {
                        developer = "Anonymous";
                    } else {
                        developer = developersJsonNode.get(0).asText();
                    }
                    Product product = new Product(name, price, shortDescription, headerImage, webSite, developer);
                    JsonNode genresJsonNode = node.get("genres");
                    if (genresJsonNode != null && !genresJsonNode.isEmpty()) {
                        Iterator<JsonNode> iterator = genresJsonNode.iterator();
                        iterator.forEachRemaining(g -> {
                            String categoryName = g.asText();
                            Long categoryId = internalCacheMap.get(categoryName);
                            product.getProductCategories().add(new ProductCategory(product, categoryId));
                        });
                    }
                    return product;
                }
                return null;
            }
        }    
        ```
        </div>
        </details>

  - Step에 TaskExecutor 적용
        <details>
        <summary><strong> CODE - Click! </strong></summary>
        <div markdown="1">
        
        ```java
        @Bean
            @JobScope
            public Step jsonConvertAndAddDatabaseJobStep(PlatformTransactionManager transactionManager,
                                                         JobRepository jobRepository)
                    throws IOException {
                return new StepBuilder("jsonConvertAndAddDatabaseJobStep", jobRepository)
                        .<Product, Product>chunk(CHUNK_SIZE, transactionManager)
                        .reader(jsonProductItemReader())
                        .writer(jpaProductItemWriter())
                        .taskExecutor(taskExecutor())
                        .build();
            } 
        ```
        </div>
        </details>
- 배치 결과 검증
    ```java
    Job: [SimpleJob: [name=jsonConvertAndAddDatabaseJob]] completed with the following parameters: [{'version':'{value=7, type=class java.lang.String, identifying=true}'}] 
    and the following status: [COMPLETED] in 12s428ms
    ```
