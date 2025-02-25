# 팀원 역할분담

| 이름 | 역할 | 구현 기능 |
| --- | --- | --- |
| 이하은 | 팀장 | Review CRUD/검색 기능,  Review 테스트 코드 작성, 프로젝트 인프라 구축 및 배포, CI/CD |
| 박성빈  | 팀원 | Order/Payment CRUD/검색 기능,  Order/Payment 테스트 코드 작성, 프로젝트 발표 |
| 이용재  | 팀원 | Member 인증/인가 기능 |
| 이지웅 | 팀원 | Item CRUD/검색 기능,  AI API 연동, Item 테스트 코드 작성 |
| 유경철 | 팀원 | Store CRUD/검색 기능, Store 테스트 코드 작성 |

# 서비스 구성 및 실행 방법

## 아키텍처

![Image](https://github.com/user-attachments/assets/6be73b5a-f393-4660-8cc8-d9a018100d43)

## 프로젝트 구조

```
+---main
|   +---java
|   |   \---com
|   |       \---sparta
|   |           \---tl3p
|   |               \---backend
|   |                   |   BackendApplication.java
|   |                   |   
|   |                   +---common
|   |                   |   +---audit
|   |                   |   |       AuditorAwareImpl.java
|   |                   |   |       BaseEntity.java
|   |                   |   |       
|   |                   |   +---config
|   |                   |   |       JpaConfig.java
|   |                   |   |       QueryDslConfig.java
|   |                   |   |       RedisConfig.java
|   |                   |   |       RestClientConfig.java
|   |                   |   |       SecurityConfig.java
|   |                   |   |       SwaggerConfig.java
|   |                   |   |       
|   |                   |   +---dto
|   |                   |   |       ErrorResponseDto.java
|   |                   |   |       SuccessResponseDto.java
|   |                   |   |       
|   |                   |   +---exception
|   |                   |   |       BusinessException.java
|   |                   |   |       
|   |                   |   +---filter
|   |                   |   |       JwtAuthenticationFilter.java
|   |                   |   |       
|   |                   |   +---handler
|   |                   |   |       GlobalExceptionHandler.java
|   |                   |   |       
|   |                   |   +---type
|   |                   |   |       Address.java
|   |                   |   |       ErrorCode.java
|   |                   |   |       ResponseCode.java
|   |                   |   |       
|   |                   |   \---util
|   |                   |           GenerateSecretKey.java
|   |                   |           JwtTokenProvider.java
|   |                   |           
|   |                   \---domain
|   |                       +---ai
|   |                       |   +---controller
|   |                       |   |       AIDescriptionController.java
|   |                       |   |       
|   |                       |   +---dto
|   |                       |   |       AIDescriptionRequestDto.java
|   |                       |   |       AIDescriptionResponseDto.java
|   |                       |   |       GeminiApiRequestDto.java
|   |                       |   |       GeminiApiResponseDto.java
|   |                       |   |       
|   |                       |   +---entity
|   |                       |   |       AIDescription.java
|   |                       |   |       
|   |                       |   +---repository
|   |                       |   |       AIDescriptionRepository.java
|   |                       |   |       
|   |                       |   \---service
|   |                       |           AIDescriptionService.java
|   |                       |           
|   |                       +---item
|   |                       |   +---controller
|   |                       |   |       ItemController.java
|   |                       |   |       
|   |                       |   +---dto
|   |                       |   |       ItemCreateRequestDto.java
|   |                       |   |       ItemPageResponseDto.java
|   |                       |   |       ItemResponseDto.java
|   |                       |   |       ItemSearchRequestDto.java
|   |                       |   |       ItemUpdateRequestDto.java
|   |                       |   |       
|   |                       |   +---entity
|   |                       |   |       Item.java
|   |                       |   |       
|   |                       |   +---enums
|   |                       |   |       ItemSortOption.java
|   |                       |   |       ItemStatus.java
|   |                       |   |       
|   |                       |   +---repository
|   |                       |   |       ItemQueryRepository.java
|   |                       |   |       ItemQueryRepositoryImpl.java
|   |                       |   |       ItemRepository.java
|   |                       |   |       
|   |                       |   \---service
|   |                       |           ItemService.java
|   |                       |           
|   |                       +---member
|   |                       |   +---controller
|   |                       |   |       MemberController.java
|   |                       |   |       
|   |                       |   +---dto
|   |                       |   |       LoginRequestDto.java
|   |                       |   |       LoginResponseDto.java
|   |                       |   |       MemberRequestDto.java
|   |                       |   |       MemberResponseDto.java
|   |                       |   |       
|   |                       |   +---entity
|   |                       |   |       CustomUserDetails.java
|   |                       |   |       Member.java
|   |                       |   |       
|   |                       |   +---enums
|   |                       |   |       MemberStatus.java
|   |                       |   |       Role.java
|   |                       |   |       
|   |                       |   +---repository
|   |                       |   |       MemberRepository.java
|   |                       |   |       
|   |                       |   \---service
|   |                       |           CustomUserDetailsService.java
|   |                       |           MemberService.java
|   |                       |           RedisService.java
|   |                       |           
|   |                       +---order
|   |                       |   +---controller
|   |                       |   |       OrderController.java
|   |                       |   |       
|   |                       |   +---dto
|   |                       |   |       OrderCancelRequestDto.java
|   |                       |   |       OrderDetailResponseDto.java
|   |                       |   |       OrderItemDetailDto.java
|   |                       |   |       OrderItemRequestDto.java
|   |                       |   |       OrderRequestDto.java
|   |                       |   |       OrderResponseDto.java
|   |                       |   |       OrderUpdateRequestDto.java
|   |                       |   |       
|   |                       |   +---entity
|   |                       |   |       Order.java
|   |                       |   |       OrderItem.java
|   |                       |   |       
|   |                       |   +---enums
|   |                       |   |       DataStatus.java
|   |                       |   |       OrderType.java
|   |                       |   |       PaymentMethod.java
|   |                       |   |       
|   |                       |   +---repository
|   |                       |   |       OrderRepository.java
|   |                       |   |       OrderRepositoryCustom.java
|   |                       |   |       OrderRepositoryImpl.java
|   |                       |   |       
|   |                       |   \---service
|   |                       |           OrderService.java
|   |                       |           
|   |                       +---payment
|   |                       |   +---controller
|   |                       |   |       PaymentController.java
|   |                       |   |       
|   |                       |   +---dto
|   |                       |   |       PaymentRequestDto.java
|   |                       |   |       PaymentResponseDto.java
|   |                       |   |       
|   |                       |   +---entity
|   |                       |   |       Payment.java
|   |                       |   |       
|   |                       |   +---enums
|   |                       |   |       PaymentMethod.java
|   |                       |   |       PaymentStatus.java
|   |                       |   |       
|   |                       |   +---repository
|   |                       |   |       PaymentRepository.java
|   |                       |   |       
|   |                       |   \---service
|   |                       |           PaymentService.java
|   |                       |           
|   |                       +---review
|   |                       |   +---controller
|   |                       |   |       ReviewController.java
|   |                       |   |       ReviewOwnerController.java
|   |                       |   |       
|   |                       |   +---dto
|   |                       |   |       ReviewCreationRequestDto.java
|   |                       |   |       ReviewItemResponseDto.java
|   |                       |   |       ReviewResponseDto.java
|   |                       |   |       ReviewUpdateRequestDto.java
|   |                       |   |       
|   |                       |   +---entity
|   |                       |   |       Review.java
|   |                       |   |       ReviewStatus.java
|   |                       |   |       
|   |                       |   +---repository
|   |                       |   |       ReviewCustomRepository.java
|   |                       |   |       ReviewCustomRepositoryImpl.java
|   |                       |   |       ReviewRepository.java
|   |                       |   |       
|   |                       |   \---service
|   |                       |           ReviewService.java
|   |                       |           
|   |                       \---store
|   |                           +---controller
|   |                           |       StoreController.java
|   |                           |       
|   |                           +---dto
|   |                           |       StoreRequestDto.java
|   |                           |       StoreResponseDto.java
|   |                           |       
|   |                           +---entity
|   |                           |       Store.java
|   |                           |       StoreCategory.java
|   |                           |       
|   |                           +---enums
|   |                           |       CategoryType.java
|   |                           |       StoreStatus.java
|   |                           |       
|   |                           +---repository
|   |                           |       StoreCategoryRepository.java
|   |                           |       StoreRepository.java
|   |                           |       
|   |                           \---service
|   |                                   StoreService.java
|   |                                   
|   \---resources
|           application.yml
|           
\---test
    \---java
        \---com
            \---sparta
                \---tl3p
                    \---backend
                        |   BackendApplicationTests.java
                        |   
                        +---item
                        |       ItemServiceTest.java
                        |       
                        +---order
                        |       OrderServiceTest.java
                        |       
                        +---review
                        |       ReviewServiceTest.java
                        |       
                        \---store
                                StoreServiceTest.java
                                

```

## 로컬 환경 구동 방법
0. mysql, elk, prometheus 개별적으로 설치 해주셔야 합니다.
   
1. Clone Project
```
git clone https://github.com/haisley77/tl1p.git
```

2. Change path to /tl1p/src/main & make resources directory
```
mkdir resources
```

3. Change path to /tl1p/src/main/resources & make application.yml file
```
server:
  port: ${SERVER_PORT}

spring:
  config:
    import:
      - optional:file:env/db.env[.properties]
      - optional:file:env/security.env[.properties]
  application:
    name: backend
  datasource:
    driver-class-name: org.postgresql.Driver
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}

  jpa:
    show-sql: true
    database: postgresql
    hibernate:
      ddl-auto: ${DATABASE_DDL_AUTO}
    redis:
      port: ${REDIS_PORT}
      host: ${REDIS_HOST}
      timeout: ${REDIS_TIMEOUT}
      lettuce:
        pool:
          max-active: ${REDIS_MAX_ACTIVE}
          max-idle: ${REDIS_MAX_IDLE}
          min-idle: ${REDIS_MIN_IDLE}

jwt:
  secret: ${JWT_SECRET_KEY}
  access-token-validity: ${ACCESS_EXPIRATION}
  refresh-token-validity: ${REFRESH_EXPIRATION}
  access-header: ${ACCESS_HEADER}
  refresh-header: ${REFRESH_HEADER}

api:
  gemini:
    key: ${AI_API_KEY}
    url: ${AI_URL_ENTRYPOINT}

```
4. Change path to tl1p & make env directory
```
mkdir env
```

5. Change path to /tl1p/env & make db.env, security.env file

db.env
```
DATABASE_URL=jdbc:postgresql://localhost:5432/tl1p
DATABASE_USERNAME=<your_local_database_username>
DATABASE_PASSWORD=<your_local_database_password>
DATABASE_DDL_AUTO=none
```

security.env
```
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_TIMEOUT=6000
REDIS_MAX_ACTIVE=10
REDIS_MAX_IDLE=10
REDIS_MIN_IDLE=2

JWT_SECRET_KEY=<>
ACCESS_EXPIRATION=1800000
REFRESH_EXPIRATION=604800000
ACCESS_HEADER=Authorization
REFRESH_HEADER=refresh:

SERVER_PORT=<prefered_server_port_to_run>

AI_API_KEY=<api_key>
AI_URL_ENTRYPOINT=<api_url>
```


# 프로젝트 목적/상세

# ERD

![{75DDD8DB-F99E-419F-AFCE-C4D9379AACB9}](https://github.com/user-attachments/assets/6a5f28d3-ac68-4f1a-b5e8-76802263be8b)


# 기술 스택

| Tech | Ver | 선정 이유 |
| --- | --- | --- |
| **Java** | `17` | SpringBoot 3과의 호환성을 고려하여 안정적인 버전 선택 |
| **SpringBoot** | `3.4.2` | 최신 기능 활용, 성능 최적화, 보안 패치 적용이 용이 |
| **Postgres** | `17` | 대용량 데이터 처리 성능과 뛰어난 확장성 |
| **Redis** | `3.4.2` | 세션 관리, 캐싱, 실시간 데이터 처리를 위한 인메모리 데이터 저장소 |
| **Spring Security** | `6.4.2` | 인증 및 권한 관리를 위한 보안 기능 제공 |
| **Json Web Token (JWT)** | `0.11.5` | 토큰 기반의 Stateless한 인증 방식으로 서버 부담을 줄이고 확장성 확보 |
| **QueryDSL** | `5.0.0` | 타입 안전성과 가독성이 높은 동적 쿼리 지원을 통해 유지보수성 향상 |
| **AWS EC2** | `Ubuntu 22.04` | 서비스 확장성을 고려한 클라우드 배포 환경, 유연한 인프라 확장 가능 |
| **Jenkins** | `2.498` | CI/CD 자동화를 통해 코드 품질 유지, 빌드 및 배포 프로세스 단순화, 팀 생산성 향상 |
