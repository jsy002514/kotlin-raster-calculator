package cli

import domain.RasterCalculator
import domain.RasterExpressionParser
import io.RasterReader
import io.GeoTiffReader
import java.io.File
import java.util.Scanner

class Application(
    private val rasterReader: RasterReader,
    private val parser: RasterExpressionParser,
    private val calculator: RasterCalculator
) {

    fun run() {
        val scanner = Scanner(System.`in`)

        try {
            println("GeoTIFF 파일이 포함된 디렉토리 경로를 입력하세요:")
            val filePath = scanner.nextLine()
            val multiRaster = rasterReader.read(File(filePath))

            println("사용 가능한 밴드: ${multiRaster.getBandNames()}")

            println("래스터 수식을 공백으로 구분해 입력하세요 (예: ( B04 + B03 ) / ( B04 - B03 )):")
            val expression = scanner.nextLine()

            val postfix = parser.parseToPostfix(expression)

            val resultRaster = calculator.execute(postfix, multiRaster)

            println("\n--- 계산 결과 ---")
            println("결과 래스터 크기: ${resultRaster.width}x${resultRaster.height}")
            println("평균(Mean) DN: ${resultRaster.meanDN()}")
            println("최소(Min) DN: ${resultRaster.minDN()}")
            println("최대(Max) DN: ${resultRaster.maxDN()}")

        } catch (e: Exception) {
            println("[ERROR] 오류가 발생했습니다: ${e.message}")
        }
    }
}


fun main() {
    val realReader: RasterReader = GeoTiffReader()

    val parser = RasterExpressionParser()
    val calculator = RasterCalculator()

    val app = Application(realReader, parser, calculator)
    app.run()
}
