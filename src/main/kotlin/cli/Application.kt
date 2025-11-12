package cli // ⭐️ 1. "cli.MainKt"의 'cli' 부분

import domain.RasterCalculator
import domain.RasterExpressionParser
import io.GeotiffReader
import io.RasterReader
import java.io.File
import java.util.Scanner

class Application(
    // [DI] TDD가 어려운 '인프라'를 인터페이스로 주입받음
    private val rasterReader: RasterReader,
    // [DI] TDD가 쉬운 '도메인 서비스'를 주입받음
    private val parser: RasterExpressionParser,
    private val calculator: RasterCalculator
) {

    fun run() {
        val scanner = Scanner(System.`in`)

        try {
            // 1. 파일 읽기 (Reader에 위임)
            println("Sentinel-2 IMG_DATA 폴더 경로를 입력하세요 (예: C:/data/sentinel_test_data/IMG_DATA):")
            val filePath = scanner.nextLine()
            val multiRaster = rasterReader.read(File(filePath))

            // 2. 밴드 정보 표시
            println("사용 가능한 밴드: ${multiRaster.getBandNames()}")

            // 3. 수식 입력 및 계산 (Parser, Calculator에 위임)
            println("래스터 수식을 입력하세요 (예: (B04 + B03) / (B04 - B03)):")
            // ⭐️ 프리코스 원칙: 공백을 포함하여 입력받도록 수정
            val expression = scanner.nextLine()

            // 3-1. 파싱 (1주차 계산기)
            val postfix = parser.parseToPostfix(expression)

            // 3-2. 계산 (로또 결과 계산기)
            val resultRaster = calculator.execute(postfix, multiRaster)

            // 4. 결과 출력
            println("\n--- 계산 결과 ---")
            println("결과 래스터 크기: ${resultRaster.width}x${resultRaster.height}")
            println("평균(Mean) DN: ${resultRaster.meanDN()}")
            println("최소(Min) DN: ${resultRaster.minDN()}")
            println("최대(Max) DN: ${resultRaster.maxDN()}")

        } catch (e: Exception) {
            // 프리코스 예외 처리 요구사항
            println("[ERROR] 오류가 발생했습니다: ${e.message}")
        }
    }
}

fun main() {

    // 1. 실제 의존성(TDD 어려운 객체) 생성
    // ⭐️ GeoTiffReader -> Sentinel2Jp2Reader로 변경
    val realReader: RasterReader = GeotiffReader()

    // 2. 도메인 서비스(TDD 쉬운 객체) 생성
    val parser = RasterExpressionParser()
    val calculator = RasterCalculator()

    // 3. 의존성 주입(DI)을 통해 Application 생성 및 실행
    val app = Application(realReader, parser, calculator)
    app.run()
}
