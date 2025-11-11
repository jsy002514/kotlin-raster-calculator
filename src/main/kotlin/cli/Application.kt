package cli

import data.MultiRaster
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

        println("래스터 파일을 입력하세요 (예: data/sample.tif):")
        val filePath = scanner.nextLine()
        val multiRaster = rasterReader.read(File(filePath))

        println("사용 가능한 밴드: ${multiRaster.getBandNames()}")

        println("래스터 수식을 공백으로 구분하여 입력하세요 (예: ( b04 + b03 ) / ( b04 - b03 )):")
        val expression = scanner.nextLine()

        val postfix = parser.parseToPostfix(expression)

        val resultRaster = calculator.execute(postfix, multiRaster)

        println("\n--- 계산 결과 ---")
        println("결과 래스터 크기: ${resultRaster.width}x${resultRaster.height}")
        println("평균(Mean) DN: ${resultRaster.meanDN()}")
        println("최소(Min) DN: ${resultRaster.minDN()}")
        println("최대(Max) DN: ${resultRaster.maxDN()}")

        //TODO: 결과 래스터 팝업으로 이미지 출력 기능 구현
    }

    fun main() {
        val realReader: RasterReader = GeoTiffReader()

        val parser = RasterExpressionParser()
        val calculator = RasterCalculator()

        try {
            val app = Application(realReader, parser, calculator)
            app.run()
        } catch (e: Exception) {
            println("[ERROR]: ${e.message}")
        }
    }
}
