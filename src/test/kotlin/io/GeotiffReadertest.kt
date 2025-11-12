package io

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class GeoTiffReaderTest {

    @Test
    fun `GeoTIFF 영상을 읽고 밴드 정보를 검증한다`() {
        // given
        val reader: RasterReader = GeoTiffReader()

        // 테스트 데이터 경로 지정
        val testDir = File(
            "C:/Users/jsy00/Desktop/KotlinStudy/kotlin-raster-calculator/src/test/resources/sentinel_test_data"
        )

        // when
        val multiRaster = reader.read(testDir)

        // then
        val expectedBands = listOf("B02", "B03", "B04", "B08")
        assertThat(multiRaster.getBandNames()).containsExactlyInAnyOrderElementsOf(expectedBands)

        val rasterB02 = multiRaster.getBandByName("B02")
        val rasterB03 = multiRaster.getBandByName("B03")
        val rasterB04 = multiRaster.getBandByName("B04")
        val rasterB08 = multiRaster.getBandByName("B08")

        // 기본 형태와 해상도 확인 (모두 동일해야 함)
        assertThat(rasterB02.width).isEqualTo(rasterB03.width)
        assertThat(rasterB02.height).isEqualTo(rasterB03.height)
        assertThat(rasterB02.width).isEqualTo(rasterB04.width)
        assertThat(rasterB02.height).isEqualTo(rasterB04.height)
        assertThat(rasterB02.width).isEqualTo(rasterB08.width)
        assertThat(rasterB02.height).isEqualTo(rasterB08.height)

        // CRS 확인 (모두 동일해야 함)
        assertThat(rasterB02.crs).isEqualTo(rasterB03.crs)
        assertThat(rasterB02.crs).isEqualTo(rasterB04.crs)
        assertThat(rasterB02.crs).isEqualTo(rasterB08.crs)

        // 픽셀 값 존재 여부
        assertThat(rasterB02.data).isNotEmpty
        assertThat(rasterB03.data).isNotEmpty
        assertThat(rasterB04.data).isNotEmpty
        assertThat(rasterB08.data).isNotEmpty
    }
}
