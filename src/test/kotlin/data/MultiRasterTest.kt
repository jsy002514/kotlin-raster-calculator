package data

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.assertj.core.api.Assertions.assertThatThrownBy

class MultiRasterTest {

    private lateinit var baseGeoTransform: GeoTransform

    private fun createMockRaster(width: Int, height: Int, fillValue: Float): Raster{
        val data = List(width * height){fillValue}
        return Raster(width, height, baseGeoTransform, values = data)
    }

    @BeforeEach
    fun setUp(){
        baseGeoTransform = GeoTransform(0.0, 0.0, 1.0)
    }

    @Test
    fun `빈_맵으로_MultiRaster_생성_시_예외_발생해야_한다`(){
        val emptyMap = emptyMap<String, Raster>()
        assertThatThrownBy {
            MultiRaster(emptyMap)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("하나 이상의 밴드가 필요합니다.")
    }

    @Test
    fun `크기가_다른_래스터로_MultiRaster_생성_시_예외_발생한다.`(){
        val rasterA = createMockRaster(2,2,1f)
        val rasterB = createMockRaster(3,3,1f)

        val bandMap = mapOf("b01" to rasterA, "b02" to rasterB)

        assertThatThrownBy {
            MultiRaster(bandMap)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("모든 밴드의 크기 및 좌표계가 동일해야 합니다.")
    }

    @Test
    fun `getBandNames가_키_목록을_정확히_반환한다`(){
        val rasterA = createMockRaster(2,2,1f)
        val rasterB = createMockRaster(2,2,2f)

        val bandMap = mapOf("b03" to rasterA, "b04" to rasterB)
        val multiRaster = MultiRaster(bandMap)

        assertThat(multiRaster.getBandNames()).containsExactlyInAnyOrder("b03","b04")
    }

    @Test
    fun `getBandByName이_정확한_래스터를_반환`(){
        val rasterA = createMockRaster(2,2,1f)
        val rasterB = createMockRaster(2,2,2f)

        val bandMap = mapOf("b03" to rasterA, "b04" to rasterB)
        val multiRaster = MultiRaster(bandMap)

        assertThat(multiRaster.getBandByName("b03")).isEqualTo(rasterA)
    }

    @Test
    fun`getBandByName이_없는_이름_호출_시_예외_발생한다`(){
        val rasterA = createMockRaster(2,2,1f)
        val bandMap = mapOf("b03" to rasterA)
        val multiRaster = MultiRaster(bandMap)

        assertThatThrownBy {
            multiRaster.getBandByName("b99")
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("'b99' 이름의 밴드를 찾을 수 없습니다.")
    }
}
