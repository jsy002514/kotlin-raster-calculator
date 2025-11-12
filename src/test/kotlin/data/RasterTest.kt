package data

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

class RasterTest {

    private lateinit var baseGeoTransform: GeoTransform

    @BeforeEach
    fun setUp() {
        baseGeoTransform = GeoTransform(0.0, 0.0, 1.0)
    }

    @Test
    fun `getValue가_좌표_범위를_벗어나면_예외가_발생한다`() {
        val raster = Raster(2, 2, baseGeoTransform, values = listOf(1f, 2f, 3f, 4f))

        assertThatThrownBy {
            raster.getValue(5, 5)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("좌표가 래스터 범위(WxH: 2 x 2)를 벗어났습니다.")
    }

    @Test
    fun `getValue가_정확한_값을_반환한다`() {
        val raster = Raster(2, 2, baseGeoTransform, values = listOf(1f, 2f, 3f, 4f))
        assertThat(raster.getValue(1, 0)).isEqualTo(2f)
        assertThat(raster.getValue(0, 1)).isEqualTo(3f)
    }

    @Test
    fun `DN_최소와_최대가_정확해야_한다`() {
        val data = listOf(10f, 5f, 15f, 20f)
        val raster = Raster(2, 2, baseGeoTransform, values = data)
        assertThat(raster.minDN()).isEqualTo(5f)
        assertThat(raster.maxDN()).isEqualTo(20f)
    }

    @Test
    fun `스칼라_덧셈_연산_및_원본의_불변성을_확인한다`() {
        val data = listOf(1f, 2f, 3f, 4f)
        val raster = Raster(2, 2, baseGeoTransform, values = data)

        val resultRaster = raster + 10f

        assertThat(resultRaster.getValue(0, 0)).isEqualTo(11f)
        assertThat(resultRaster.getValue(1, 1)).isEqualTo(14f)

        assertThat(raster.getValue(0, 0)).isEqualTo(1f)
    }

    @Test
    fun `크기가_다른_래스터_연산_시_예외가_발생한다`() {
        val rasterA = Raster(2, 2, baseGeoTransform, values = listOf(1f, 2f, 3f, 4f))
        val rasterB = Raster(1, 1, baseGeoTransform, values = listOf(10f))

        assertThatThrownBy {
            rasterA + rasterB
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("두 래스터의 크기가 다릅니다")
    }

    @Test
    fun `래스터_간_나눗셈_시_0으로_나누면_0f를_반환한다`(){
        val rasterA = Raster(2, 2, baseGeoTransform, values = listOf(10f, 10f, 10f, 10f))
        val rasterB = Raster(2, 2, baseGeoTransform, values = listOf(2f, 0f, 5f, 0f))

        val resultRaster = rasterA / rasterB

        assertThat(resultRaster.getValue(0, 0)).isEqualTo(5f)
        assertThat(resultRaster.getValue(1, 0)).isEqualTo(0f)
    }
}
