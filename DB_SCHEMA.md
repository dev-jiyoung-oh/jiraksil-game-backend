
# jiraksil_game 데이터베이스 설계

```
- 1 ~ 4. 기상미션(WAKE_UP_MISSION)
- 5 ~ 11. 몸으로 말해요(CHARADES)
```

## 1 ~ 4. 기상 미션(WAKE_UP_MISSION)
### 1. WAKE_UP_MISSION_GAMES (기상 미션 게임)
| 컬럼명       | 타입           | 필수 | 고유 | 설명                         |
|--------------|---------------|------|------|------------------------------|
| id           | BIGINT        | Y    | Y    | 게임 ID (PK)                 |
| code         | VARCHAR(255)  | Y    | Y    | 게임 코드 (외부 노출용)        |
| wake_up_time | DATETIME      |      |      | 기상 시간                     |
| contacts     | VARCHAR(1000) |      |      | 연락처 목록 (쉼표 구분 문자열) |
| password_hash| VARCHAR(255)  | Y    |      | 비밀번호 해시                 |
| created_at   | DATETIME      | Y    |      | 생성 시간                     |
| updated_at   | DATETIME      |      |      | 수정 시간                     |

### 2. WAKE_UP_MISSION_GAME_MISSIONS (게임-미션 매핑)
| 컬럼명          | 타입        | 필수 | 고유 | 설명                       |
|-----------------|-------------|------|------|----------------------------|
| id             | BIGINT      | Y    | Y    | 매핑 ID (PK)               |
| game_id        | BIGINT      | Y    |      | 기상 미션 게임 ID (FK)     |
| mission_id     | BIGINT      | Y    |      | 미션 ID (FK)               |
| assigned_player| INT         | Y    |      | 해당 미션을 수행할 플레이어 (인덱스) |

### 3. WAKE_UP_MISSION_MISSIONS (기상 미션 목록)
| 컬럼명     | 타입        | 필수 | 고유 | 설명                      |
|------------|-------------|------|------|---------------------------|
| id         | BIGINT      | Y    | Y    | 미션 ID (PK)              |
| content    | TEXT        | Y    |      | 미션 내용                 |

### 4. RANDOM_LOCATIONS (랜덤 장소)
| 컬럼명     | 타입        | 필수 | 고유 | 설명                      |
|------------|-------------|------|------|---------------------------|
| id         | BIGINT      | Y    | Y    | 장소 ID (PK)              |
| name       | VARCHAR(100)| Y    |      | 장소 이름 (예: 거실 테레비 앞) |
| fallback   | TEXT        |      |      | 차선책 또는 추가 안내 메시지 |


## 5 ~ 11. 몸으로 말해요(CHARADES)
### 5. CHARADES_GAMES (몸으로 말해요 게임)
| 컬럼명             | 타입                                                      | 필수 | 고유 | 설명                             |
| ----------------- | ---------------------------------------------------------- | -- | -- | ------------------------------ |
| id                | BIGINT                                                     | Y  | Y  | 게임 ID (PK)                     |
| code              | VARCHAR(32)                                                | Y  | Y  | 게임 코드 (외부 노출용)                  |
| mode              | ENUM('LIMITED','UNTIL_CLEAR')                              | Y  |    | 게임 모드 (제한시간 / 다 맞추기)           |
| duration_sec      | INT                                                        |    |    | 제한시간(초), LIMITED 모드에서만 의미      |
| target_count      | INT                                                        |    |    | 목표 문제 수, UNTIL_CLEAR 모드에서만 의미 |
| pass_limit        | INT                                                        | Y  |    | 턴당 패스 허용 횟수                    |
| rounds_per_team   | INT                                                        | Y  |    | 팀당 라운드 수                       |
| status            | ENUM('READY','PLAYING','PAUSED','INTERMISSION','FINISHED') | Y  |    | 게임 상태                          |
| created_at        | DATETIME                                                   | Y  |    | 생성 시간                          |
| updated_at        | DATETIME                                                   |    |    | 수정 시간                          |


### 6. CHARADES_TEAMS (게임 팀)
| 컬럼명       | 타입                                 | 필수 | 고유 | 설명                          |
| ------------ | ----------------------------------- | -- | -- | --------------------------- |
| id           | BIGINT                              | Y  | Y  | 팀 ID (PK)                   |
| game_id      | BIGINT                              | Y  |    | 게임 ID (FK)                  |
| code         | CHAR(1)                             | Y  |    | 팀 코드(A~Z 1글자), 게임 내 유일     |
| name         | VARCHAR(100)                        | Y  |    | 팀명 (미입력 시 `Team {code}` 자동) |
| color        | VARCHAR(32)                         | Y  |    | 팀 색상 (예: 'BLUE','RED','GREEN','YELLOW') |
| score        | INT                                 | Y  |    | 누적 점수 (정답 수)                 |
| order_index  | INT                                 | Y  |    | 진행 순서 (0부터 시작)               |
| created_at   | DATETIME                            | Y  |    | 생성 시간                       |
| updated_at   | DATETIME                            |    |    | 수정 시간                       |

### 7. CHARADES_CATEGORIES (카테고리 마스터)
| 컬럼명      | 타입        | 필수 | 고유 | 설명                       |
| ---------- | ----------- | -- | -- | ------------------------ |
| id         | SMALLINT    | Y  | Y  | 카테고리 ID (PK)             |
| code       | VARCHAR(32) | Y  | Y  | 카테고리 코드 (외부 노출용) |
| name       | VARCHAR(50) | Y  | Y  | 카테고리명 (표시용)               |
| is_active  | TINYINT(1)  | Y  |    | 사용 여부                    |

### 8. CHARADES_GAME_CATEGORIES (게임-카테고리 매핑)
| 컬럼명        | 타입     | 필수 | 고유 | 설명           |
| ------------ | -------- | -- | -- | ------------ |
| id           | BIGINT   | Y  | Y  | 매핑 ID (PK)   |
| game_id      | BIGINT   | Y  |    | 게임 ID (FK)   |
| category_id  | SMALLINT | Y  |    | 카테고리 ID (FK) |

### 9. CHARADES_WORDS (제시어 사전)
| 컬럼명        | 타입        | 필수 | 고유 | 설명           |
| ------------ | ------------ | -- | -- | ------------ |
| id           | BIGINT       | Y  | Y  | 단어 ID (PK)   |
| category_id  | SMALLINT     | Y  |    | 카테고리 ID (FK) |
| text         | VARCHAR(255) | Y  |    | 단어 텍스트       |
| description  | VARCHAR(255) |    |    | 단어 설명       |
| is_active    | TINYINT(1)   | Y  |    | 사용 여부        |
| created_at   | DATETIME     | Y  |    | 생성 시간        |

### 10. CHARADES_TURNS (턴)
| 컬럼명         | 타입                          | 필수 | 고유 | 설명                   |
| ------------- | ----------------------------- | -- | -- | ------------------------ |
| id            | BIGINT                        | Y  | Y  | 턴 ID (PK)               |
| code          | VARCHAR(32)                   |    | Y  | 턴 코드 (외부 노출용)      |
| game_id       | BIGINT                        | Y  |    | 게임 ID (FK)             |
| team_id       | BIGINT                        | Y  |    | 팀 ID (FK)               |
| round_index   | INT                           | Y  |    | 팀 기준 라운드 인덱스 (0부터) |
| play_no       | INT                           | Y  |    | 게임 플레이 번호 (1부터) |
| mode          | ENUM('LIMITED','UNTIL_CLEAR') | Y  |    | 턴 스냅샷 모드            |
| duration_sec  | INT                           |    |    | LIMITED 전용 제한시간(초) |
| target_count  | INT                           |    |    | UNTIL_CLEAR 전용 목표 문제 수 |
| pass_limit    | INT                           | Y  |    | 턴당 패스 허용 (스냅샷)    |
| started_at    | DATETIME                      |    |    | 턴 시작 시간              |
| ended_at      | DATETIME                      |    |    | 턴 종료 시간              |
| correct_count | INT                           |    |    | 맞힌 개수                 |
| used_pass     | INT                           |    |    | 사용한 패스 수             |
| time_used_sec | INT                           |    |    | LIMITED 모드 소요 시간 (= duration_sec - 남은 시간) |
| elapsed_sec   | INT                           |    |    | UNTIL_CLEAR 모드 경과 시간 |

### 11. CHARADES_TURN_WORDS (턴 내 제시어)
| 컬럼명       | 타입                   | 필수 | 고유 | 설명                       |
| ----------- | ---------------------- | -- | -- | --------------------------- |
| id          | BIGINT                 | Y  | Y  | 항목 ID (PK)                  |
| turn_id     | BIGINT                 | Y  |    | 턴 ID (FK)                   |
| idx         | INT                    | Y  |    | 턴 내 순번(1부터 시작)              |
| word_id     | BIGINT                 |    |    | 단어 ID (사전 기반일 때), 삭제 시 NULL |
| word_text   | VARCHAR(255)           | Y  |    | 당시 표시된 단어 스냅샷               |
| action      | ENUM('CORRECT','PASS') | Y  |    | 판정 종류                       |
| at_sec      | INT                    | Y  |    | 이벤트 발생 시각(초)                |
| created_at  | DATETIME               | Y  |    | 생성 시간                       |



---

## * 참고 사항

### 1. 문자 인코딩
- 데이터베이스는 `utf8mb4` 문자 집합과 `utf8mb4_unicode_ci` 정렬 방식을 사용함
  - `utf8mb4`는 이모지 등 4바이트 문자를 지원
  - `utf8mb4_unicode_ci`는 국제 표준 유니코드 규칙 기반으로 문자열 비교 수행 (언어 간 비교 정확성 ↑)

### 2. 제약 및 인덱스

- FK 제약조건
  - WAKE_UP_MISSION_GAME_MISSIONS.game_id → WAKE_UP_MISSION_GAMES.id (ON DELETE CASCADE)
  - WAKE_UP_MISSION_GAME_MISSIONS.mission_id → WAKE_UP_MISSION_MISSIONS.id (ON DELETE NO ACTION)
  - CHARADES_TEAMS.game_id → CHARADES_GAMES.id (ON DELETE CASCADE)
  - CHARADES_GAME_CATEGORIES.game_id → CHARADES_GAMES.id (ON DELETE CASCADE)
  - CHARADES_GAME_CATEGORIES.category_id → CHARADES_CATEGORIES.id (ON DELETE RESTRICT)
  - CHARADES_WORDS.category_id → CHARADES_CATEGORIES.id (ON DELETE RESTRICT)
  - CHARADES_TURNS.game_id → CHARADES_GAMES.id (ON DELETE CASCADE)
  - CHARADES_TURNS.team_id → CHARADES_TEAMS.id (ON DELETE CASCADE)
  - CHARADES_TURN_WORDS.turn_id → CHARADES_TURNS.id (ON DELETE CASCADE)
  - CHARADES_TURN_WORDS.word_id → CHARADES_WORDS.id (ON DELETE SET NULL)

- UNIQUE / CHECK
  - CHARADES_TEAMS (game_id, code) UNIQUE  / code CHECK: ^[A-Z]$ (A~Z 1글자)
  - CHARADES_TEAMS (game_id, order_index) UNIQUE
  - CHARADES_GAME_CATEGORIES (game_id, category_id) UNIQUE
  - CHARADES_WORDS (category_id, text) UNIQUE
  - CHARADES_TURNS (game_id, team_id, round_index) UNIQUE
  - CHARADES_TURN_WORDS (turn_id, idx) UNIQUE