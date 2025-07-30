
# jiraksil_game 데이터베이스 설계

## 1. WAKE_UP_MISSION_GAMES (기상 미션 게임)
| 컬럼명       | 타입           | 필수 | 고유 | 설명                         |
|--------------|---------------|------|------|------------------------------|
| id           | BIGINT        | Y    | Y    | 게임 ID (PK)                 |
| code         | VARCHAR(255)  | Y    | Y    | 게임 코드 (외부 노출용)        |
| wake_up_time | DATETIME      | N    |      | 기상 시간                     |
| contacts     | VARCHAR(1000) | N    |      | 연락처 목록(쉼표 구분 문자열)  |
| password_hash| VARCHAR(255)  | Y    |      | 비밀번호 해시                 |
| created_at   | DATETIME      | Y    |      | 생성 시간                     |
| updated_at   | DATETIME      |      |      | 수정 시간                     |

## 2. WAKE_UP_MISSION_GAME_MISSIONS (게임-미션 매핑)
| 컬럼명          | 타입        | 필수 | 고유 | 설명                       |
|-----------------|-------------|------|------|----------------------------|
| id             | BIGINT      | Y    | Y    | 매핑 ID (PK)               |
| game_id        | BIGINT      | Y    |      | 기상 미션 게임 ID (FK)     |
| mission_id     | BIGINT      | Y    |      | 미션 ID (FK)               |
| assigned_player| INT         | Y    |      | 해당 미션을 수행할 플레이어(인덱스) |

## 3. WAKE_UP_MISSION_MISSIONS (기상 미션 목록)
| 컬럼명     | 타입        | 필수 | 고유 | 설명                      |
|------------|-------------|------|------|---------------------------|
| id         | BIGINT      | Y    | Y    | 미션 ID (PK)              |
| content    | TEXT        | Y    |      | 미션 내용                 |

## 4. RANDOM_LOCATIONS (랜덤 장소)
| 컬럼명     | 타입        | 필수 | 고유 | 설명                      |
|------------|-------------|------|------|---------------------------|
| id         | BIGINT      | Y    | Y    | 장소 ID (PK)              |
| name       | VARCHAR(100)| Y    |      | 장소 이름 (예: 거실 테레비 앞) |
| fallback   | TEXT        | N    |      | 차선책 또는 추가 안내 메시지 |

---

## * 참고 사항

### 1. 문자 인코딩
- 데이터베이스는 `utf8mb4` 문자 집합과 `utf8mb4_unicode_ci` 정렬 방식을 사용함
  - `utf8mb4`는 이모지 등 4바이트 문자를 지원
  - `utf8mb4_unicode_ci`는 국제 표준 유니코드 규칙 기반으로 문자열 비교 수행 (언어 간 비교 정확성 ↑)

### 2. 외래 키(FK) 제약조건
- `WAKE_UP_MISSION_GAME_MISSIONS.game_id` → `WAKE_UP_MISSION_GAMES.id`  
  - `ON DELETE CASCADE` 적용: 게임 삭제 시 연결된 매핑 데이터도 함께 삭제됨
- `WAKE_UP_MISSION_GAME_MISSIONS.mission_id` → `WAKE_UP_MISSION_MISSIONS.id`  
  - `ON DELETE NO ACTION` 적용: 미션 삭제 시 기존 매핑 데이터는 유지됨