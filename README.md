# 우아한테크코스 오픈미션
# RasterCalculator - Kotlin 기반 위성영상 래스터 계산기
**RasterCalculator**는 Kotlin으로 개발된 위성영상 래스터 계산 도구입니다.
GeoTIFF 파일을 파싱하여 간단한 맵 알제브라(Map Algebra) 연산을 수행할 수 있도록 구현하였습니다.
초기 버전은 CLI(터미널) 기반으로 동작하며, 이후 GUI 확장을 목표로 합니다.

---

## 🚀 프로젝트 목표 및 주요 구현 성과

이 프로젝트는 GeoTools의 복잡한 의존성 문제를 해결하고, 객체 지향 원칙을 적용하여 안정적이고 테스트 가능한 래스터 계산 시스템을 구축하는 데 집중했습니다.

1.  **GeoTIFF 데이터 처리:** GeoTIFF (TIF) 포맷을 직접 파싱하며, 불안정한 네이티브 의존성(GDAL) 없이 순수 JVM 환경에서 데이터를 안전하게 읽어 처리합니다.
2.  **클린 아키텍처:** Kotlin 언어로 **불변성**과 **단일 책임 원칙(SRP)**을 준수하는 `Raster` 클래스 구조를 설계합니다.
3.  **맵 알제브라 구현:** NDVI 등 기본적인 맵 알제브라 연산을 **계산기 파서(Parser)**를 통해 분리하여 실행합니다.

---

## 시스템 구성도

```
    kotlin-raster-calculator
    |-src/
    |   |-main/kotlin/
    |      |-data/
    |          |-Raster.kt
    |          |-MultiRaster.kt
    |          |-GeoTransform.kt
    |          |-RasterData.kt
    |      |-io/
    |          |-GeoTiffReader.kt
    |          |-RasterReader.kt
    |      |-cli/
    |          |-Application.kt
    |   |-test
    |       |-kotlin
    |           |-data
    |               |-MultiRasterTest.kt
    |               |-RasterDataTest.kt
    |               |-RaterTest.kt
    |           |-domain
    |               |-RasterCalculatorTest.kt
    |               |-RasterExpressionParserTest.kt
    |           |-io
    |               |-GeotiffReaderTest.kt
    |       |-resources
    |           |-sentinel_test_data
    |-README.md
    |-build.gradle.kts 
    |-settings.gradle.kts
```

---

## 📝 구현 기능 목록

### 1. 데이터 모델 (Domain)

| 클래스 | 역할 및 특징 |
| :--- | :--- |
| **Raster** | 위성영상의 단일 밴드를 표현하는 **불변 객체**. 픽셀 데이터(`RasterData`), 좌표계, 공간 변환 정보 포함. |
| **MultiRaster** | 여러 `Raster` 밴드를 `Map<BandName, Raster>` 형태로 조합하여 관리. 밴드 간 연산을 위한 기반 제공. |
| **GeoTransform** | GeoTIFF 파일에서 추출한 공간 변환 정보(Origin, Pixel Size)를 저장하는 값 객체(Value Object). |

### 2. 입출력 및 계산 로직 (I/O & Service)

| 클래스 | 역할 및 특징 |
| :--- | :--- |
| **GeoTiffReader** | 지정된 디렉토리에서 TIF 파일들을 읽어 `MultiRaster`를 생성. GeoTools 리소스(`dispose()`)를 안전하게 해제. |
| **RasterExpressionParser** | 사용자 입력 CLI 수식(`(B08 - B04) / (B08 + B04)`)을 후위 표기법(Postfix)으로 파싱하여 계산 준비. |
| **RasterCalculator** | 파싱된 후위 표기법을 스택 기반으로 실행하여 픽셀 단위 맵 알제브라 연산을 수행. |

---

## 🚀 실행 및 테스트

### 1. 실행 방법

CLI에서 `run` 작업을 실행하고 TIF 파일이 있는 디렉토리 경로와 수식을 입력하여 실행합니다.

```
bash
./gradlew run
```

### 2. 테스트 방법
모든 단위 테스트 및 리더(Reader)의 통합 테스트는 Gradle을 통해 실행 가능합니다.

```
bash
./gradlew test
```

⌨️ CLI 입력 예시

````
GeoTIFF 파일이 포함된 디렉토리 경로를 입력하세요:
[USER_INPUT] src/test/resources/sentinel_test_data

사용 가능한 밴드: [B02, B03, B04, B08]

래스터 수식을 입력하세요 (예: ( B08 - B04 ) / ( B08 + B04 )):
[USER_INPUT] ( B08 - B04) / ( B08 + B04 )

--- 계산 결과 ---
결과 래스터 크기: 1250x1074
평균(Mean) DN: 0.69266003
최소(Min) DN: -1.0
최대(Max) DN: 1.0
````
