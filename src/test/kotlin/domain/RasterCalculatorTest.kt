package domain

import data.Raster
import data.GeoTransform
import data.MultiRaster
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.offset

class RasterCalculatorTest {

    private lateinit var calculator: RasterCalculator
    private lateinit var baseGeoTransform: GeoTransform

    private fun createMockRaster(width: Int, height: Int, values: List<Float>):Raster{
        return Raster(width, height, baseGeoTransform, values = values)
    }

    @BeforeEach
    fun setUp(){
        calculator = RasterCalculator()
        baseGeoTransform = GeoTransform(0.0,0.0, 1.0)
    }

    @Test
    fun `후위표기법으로_덧셈_연산을_정상적으로_수행한다`(){
        val rasterB03 = createMockRaster(2,1,listOf(1f,1f))
        val rasterB04 = createMockRaster(2,1,listOf(2f,2f))

        val multiRaster = MultiRaster(mapOf("b03" to rasterB03, "b04" to rasterB04))
        val postfix = listOf("b03", "b04", "+")

        val result = calculator.execute(postfix, multiRaster)

        assertThat(result.getValue(0,0)).isEqualTo(3f)
        assertThat(result.getValue(1,0)).isEqualTo(3f)
    }

    @Test
    fun `후위표기법으로_복잡한_연산을_정상적으로_수행한다`(){
        //NDVI 지수 연산으로 테스트
        val rasterB03 = createMockRaster(2,1,listOf(10f, 20f))
        val rasterB04 = createMockRaster(2,1,listOf(50f, 40f))

        val multiRaster = MultiRaster(mapOf("b03" to rasterB03, "b04" to rasterB04))
        val postfix = listOf("b04", "b03", "-", "b04", "b03", "+","/")

        val result = calculator.execute(postfix, multiRaster)

        assertThat (result.getValue(0,0)).isCloseTo(0.666f, offset(0.01f))
        assertThat (result.getValue(1,0)).isCloseTo(0.333f, offset(0.01f))
    }
}
