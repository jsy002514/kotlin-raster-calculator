# 우아한테크코스 오픈미션
# RasterCalculator - Kotlin 기반 위성영상 래스터 계산기
**RasterCalculator**는 Kotlin으로 개발된 위성영상 래스터 계산 도구입니다.
GeoTIFF 파일을 파싱하여 간단한 맵 알제브라(Map Algebra) 연산을 수행할 수 있도록 구현하였습니다.
초기 버전은 CLI(터미널) 기반으로 동작하며, 이후 GUI 확장을 목표로 합니다.

---

## 프로젝트 목표
- GeoTIFF 형식의 위성영상 데이터를 **직접 파싱**하고 이해한다.
- Kotlin 언어로 **Raster 클래스 구조**를 설계한다.
- NDVI 등 기본적인 **맵 알제브라 연산**을 구현한다.
- 시간이 허락하면 GUI(Compose Desktop) 버전으로 확장한다.

---

## 시스템 구성도

```
    kotlin-raster-calculator
    |-src/
    |   |-main/kotlin/
    |      |-data/
    |      |   |-Raster.kt
    |      |   |-MultiRaster.kt
    |      |   |-GeoTransform.kt
    |      |-io/
    |      |   |-GeoTiffReader.kt
    |      |
    |      |-cli/
    |           |-Main.kt
    |-README.md
    |-build.gradle.kts 
```

---

## 구현 기능 목록
1. Raster(단일 밴드)
    - 위성영상의 한 밴드를 표현하는 클래스
    - DN(Digital Number) 값을 2차원 배열로 저장
    - 좌표계 및 지리적 위치(GeoTransform) 정보를 포함
2. MultiRaster (멀티 밴드)
    - 여러 개의 `Raster` 밴드를 조합하여 표현
    - 밴드 간 계산을 수행할 때 사용
3. GroTransform
    - GeoTIFF 파일에서 추출한 공간 변환 정보를 저장
    - 이미지 픽셀 좌표(x,y) -> 위경도 좌표(long, lat)변환에 사용
