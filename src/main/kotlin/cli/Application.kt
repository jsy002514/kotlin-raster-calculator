package cli // ⭐️ 1. "cli.MainKt"의 'cli' 부분

import domain.RasterCalculator
import domain.RasterExpressionParser
import io.RasterReader
import io.GeoTiffReader // ⭐️ [수정] 이 줄을 추가합니다.
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
            // ⭐️ GeoTiffReader는 디렉토리가 아닌 '단일 파일 경로'를 기대합니다.
            // (이전 대화에서 TIF 파일 여러 개를 담은 디렉토리로 인터페이스를 수정했으므로, 주석만 수정합니다.)
            println("GeoTIFF 파일이 포함된 디렉토리 경로를 입력하세요:")
            val filePath = scanner.nextLine()
            val multiRaster = rasterReader.read(File(filePath))

            // 2. 밴드 정보 표시
            println("사용 가능한 밴드: ${multiRaster.getBandNames()}")

            // 3. 수식 입력 및 계산 (Parser, Calculator에 위임)
            println("래스터 수식을 입력하세요 (예: (B04 + B03) / (B04 - B03)):")
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

/**
 * ⭐️ 2. 애플리케이션의 진입점 (Entry Point)
 */
fun main() {
    // ⭐️ [수정] Unresolved reference 해결 (import io.GeoTiffReader)
    val realReader: RasterReader = GeoTiffReader()

    // 2. 도메인 서비스(TDD 쉬운 객체) 생성
    val parser = RasterExpressionParser()
    val calculator = RasterCalculator()

    // 3. 의존성 주입(DI)을 통해 Application 생성 및 실행
    val app = Application(realReader, parser, calculator)
    app.run()
}
