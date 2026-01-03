# Extension Filter

파일 확장자 필터링을 관리하는 웹 애플리케이션입니다. 고정 확장자와 커스텀 확장자를 관리하여 특정 확장자의 파일 업로드를 제어할 수 있습니다.

## 주요 기능

### 고정 확장자 관리
- 7개의 고정 확장자 제공 (bat, cmd, com, cpl, exe, scr, js)
- 각 확장자별 체크박스를 통한 활성화/비활성화
- 체크된 확장자는 필터링 대상으로 설정

### 커스텀 확장자 관리
- 사용자 정의 확장자 추가 (최대 200개)
- 확장자명: 영문 소문자/숫자 조합, 최대 20자
- 대소문자 구분 없이 저장 (자동 소문자 변환)
- 중복 확장자 검증
- 개별 확장자 삭제 기능

### 데이터 관리
- Redis를 통한 설정 정보 관리 (최대 커스텀 확장자 개수)
- MySQL을 통한 영구 데이터 저장
- 애플리케이션 시작 시 고정 확장자 자동 초기화

## 기술 스택

### Backend
- Java 21
- Spring Boot 3.4.1
- Spring Data JPA
- Spring Data Redis
- MySQL 8.0
- Redis 7
- Gradle

### Frontend
- React 18.3.1
- Vite 6.0.5
- Axios 1.7.9

### Testing
- JUnit 5
- Mockito
- Spring Boot Test
- Testcontainers (MySQL, Redis)

### Infrastructure
- Docker Compose

## 프로젝트 구조

```
extension-filter/
├── src/
│   ├── main/
│   │   ├── java/extension/filter/
│   │   │   ├── common/           # 공통 모듈 (예외, 응답 포맷)
│   │   │   ├── config/           # 설정 (Web, CORS, Redis)
│   │   │   ├── controller/       # REST API 컨트롤러
│   │   │   ├── dto/              # 요청/응답 DTO
│   │   │   ├── entity/           # JPA 엔티티
│   │   │   ├── repository/       # JPA 레포지토리
│   │   │   └── service/          # 비즈니스 로직
│   │   └── resources/
│   │       └── application.yml   # 애플리케이션 설정
│   └── test/                     # 테스트 코드
├── frontend/                     # React 프론트엔드
│   ├── src/
│   │   ├── App.jsx              # 메인 컴포넌트
│   │   └── main.jsx             # 엔트리 포인트
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml            # Docker 컨테이너 설정
├── build.gradle
└── README.md
```

## 시작하기

### 사전 요구사항
- Java 21
- Node.js 18+
- Docker & Docker Compose

### 1. 저장소 클론
```bash
git clone https://github.com/yourusername/extension-filter.git
cd extension-filter
```

### 2. MySQL & Redis 실행
```bash
docker-compose up -d
```

### 3. 백엔드 실행
```bash
./gradlew bootRun
```

백엔드 서버가 http://localhost:8080 에서 실행됩니다.

### 4. 프론트엔드 실행
```bash
cd frontend
npm install
npm run dev
```

프론트엔드가 http://localhost:3000 에서 실행됩니다.

### 5. 애플리케이션 접속
브라우저에서 http://localhost:3000 으로 접속합니다.

## API 명세

### 고정 확장자

#### 모든 고정 확장자 조회
```http
GET /api/extensions/fixed
```

**응답**
```json
{
  "meta": {
    "code": "SUCCESS",
    "message": "요청이 성공적으로 처리되었습니다."
  },
  "data": {
    "items": [
      {
        "id": 1,
        "extension": "bat",
        "checked": false
      }
    ],
    "total": 7
  }
}
```

#### 고정 확장자 체크 상태 수정
```http
PATCH /api/extensions/fixed/{extension}
Content-Type: application/json

{
  "checked": true
}
```

### 커스텀 확장자

#### 모든 커스텀 확장자 조회
```http
GET /api/extensions/custom
```

**응답**
```json
{
  "meta": {
    "code": "SUCCESS",
    "message": "요청이 성공적으로 처리되었습니다."
  },
  "data": {
    "items": [
      {
        "id": 1,
        "extension": "pdf"
      }
    ],
    "total": 1
  }
}
```

#### 커스텀 확장자 추가
```http
POST /api/extensions/custom
Content-Type: application/json

{
  "extension": "pdf"
}
```

**제약사항**
- 영문 소문자/숫자만 허용
- 최대 20자
- 최대 200개까지 추가 가능
- 중복 불가

#### 커스텀 확장자 삭제
```http
DELETE /api/extensions/custom/{id}
```

## 테스트

### 전체 테스트 실행
```bash
./gradlew test
```

### 테스트 구성
- **Controller Test**: `@WebMvcTest`를 사용한 컨트롤러 계층 테스트
- **Service Test**: `@MockitoExtension`을 사용한 서비스 계층 단위 테스트
- **Testcontainers**: MySQL, Redis 컨테이너를 활용한 통합 테스트 지원

## 주요 설정

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/extension_filter
    username: user
    password: password

  jpa:
    hibernate:
      ddl-auto: update

  data:
    redis:
      host: localhost
      port: 6379

cors:
  allowed-origins: http://localhost:3000,http://localhost:8080
```

### Docker Compose
- MySQL 8.0 (포트: 3306)
- Redis 7 (포트: 6379)

## 주요 구현 특징

### 백엔드
- **통합 응답 포맷**: `SuccessResponseAdvice`를 통한 일관된 API 응답 구조
- **예외 처리**: `@RestControllerAdvice`를 통한 전역 예외 처리
- **Validation**: Bean Validation을 통한 입력 검증
- **Redis 캐싱**: 설정 정보 Redis 저장 및 조회
- **초기 데이터**: `ApplicationRunner`를 통한 고정 확장자 자동 초기화

### 프론트엔드
- **상태 관리**: React Hooks (useState, useEffect)
- **API 통신**: Axios를 통한 RESTful API 연동
- **반응형 UI**: 그리드 레이아웃을 통한 반응형 디자인
- **에러 핸들링**: API 에러에 대한 사용자 친화적 메시지 표시

## 라이선스

MIT License