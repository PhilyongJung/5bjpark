# 5부제 알람

차량 번호판 기반 5부제 당일 알람 안드로이드 앱

## 기능

- **번호판 입력**: 차량 번호판 마지막 4자리 입력 → 5부제 해당 끝자리 자동 계산
- **5부제 당일 알람**: 여러 개 알람 추가/삭제/시간변경 가능 (5부제 날에만 울림)
- **전날 알림**: 5부제 전날 사용자 지정 시간에 사전 알림
- **월간 캘린더**: 이번 달 5부제 해당일 하이라이트 표시
- **재부팅 복구**: 기기 재부팅 후 알람 자동 재등록

## 5부제 규칙

- 날짜(일)의 **끝자리**와 번호판 마지막 숫자가 일치하면 5부제
- 예: 번호판 `3204` → 마지막 숫자 `4` → 해당 끝자리 `4`, `9`
- 매달 4일, 9일, 14일, 19일, 24일, 29일이 5부제 해당
- **평일(월~금)만 적용** — 주말 제외

## 스크린샷

| 번호판 입력 | 알람 설정 | 홈 화면 |
|:-:|:-:|:-:|
| 번호판 4자리 입력 및 캘린더 | 다중 알람 & 전날 알림 설정 | 오늘 상태 & 월간 캘린더 |

## 기술 스택

| 항목 | 기술 |
|------|------|
| 언어 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 아키텍처 | MVVM |
| 데이터 저장 | DataStore Preferences + JSON |
| 알람 | AlarmManager (`setAlarmClock()`) |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |

## 빌드

```bash
# 프로젝트 클론
git clone https://github.com/PhilyongJung/5bjpark.git
cd 5bjpark

# local.properties 설정
cp local.properties.example local.properties
# sdk.dir 경로를 본인 환경에 맞게 수정

# 빌드
./gradlew assembleDebug
```

APK 경로: `app/build/outputs/apk/debug/app-debug.apk`

## 프로젝트 구조

```
app/src/main/java/com/fivepartday/alarm/
├── FivePartDayApp.kt              # Application (알림 채널 생성)
├── MainActivity.kt                # Single Activity
├── data/
│   ├── model/
│   │   ├── AlarmItem.kt           # 개별 알람 데이터
│   │   └── UserPreferences.kt     # 사용자 설정 데이터
│   └── UserPreferencesRepository.kt
├── domain/
│   ├── FivePartDayCalculator.kt   # 5부제 판별 로직
│   └── AlarmScheduler.kt          # 스케줄링 인터페이스
├── scheduler/
│   └── AlarmSchedulerImpl.kt      # AlarmManager 구현
├── receiver/
│   ├── AlarmReceiver.kt           # 알람 수신
│   ├── NightReminderReceiver.kt   # 전날 알림 수신
│   ├── BootCompletedReceiver.kt   # 재부팅 복구
│   └── AlarmDismissReceiver.kt    # 알람 해제
├── service/
│   └── AlarmSoundService.kt       # 포그라운드 서비스 (소리+진동)
├── ui/
│   ├── theme/
│   ├── navigation/AppNavigation.kt
│   ├── screen/
│   │   ├── setup/                 # 번호판 입력 화면
│   │   ├── alarmconfig/           # 알람 설정 화면
│   │   ├── home/                  # 홈 화면
│   │   └── alarmring/             # 알람 울림 화면
│   └── component/
│       ├── LicensePlateInput.kt
│       ├── AlarmItemCard.kt
│       ├── MonthCalendarView.kt
│       └── TimePickerDialog.kt
└── util/
    ├── NotificationHelper.kt
    └── PermissionHelper.kt
```
