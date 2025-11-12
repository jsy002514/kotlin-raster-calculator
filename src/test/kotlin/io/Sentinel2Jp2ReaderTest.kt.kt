package io

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class Sentinel2Jp2ReaderTest {

    @Test
    fun `Sentinel-2 폴더를_읽고_리샘플링해야_한다`() {

        val reader: RasterReader = Sentinel2Jp2Reader(targetResolution = 10)

        val resourceDir = File(ClassLoader.getSystemResource("sentinel_test_data/IMG_DATA").toURI())

        val multiRaster = reader.read(resourceDir)

        assertThat(multiRaster.getBandNames()).contains("b04", "b05", "b01") // (10m, 20m, 60m 밴드 예시)

        val rasterB04 = multiRaster.getBandByName("b04") // 10m
        val rasterB05 = multiRaster.getBandByName("b05") // 20m -> 10m
        val rasterB01 = multiRaster.getBandByName("b01") // 60m -> 10m

        assertThat(rasterB05.width).isEqualTo(rasterB04.width)
        assertThat(rasterB05.height).isEqualTo(rasterB04.height)

        assertThat(rasterB01.width).isEqualTo(rasterB04.width)
        assertThat(rasterB01.height).isEqualTo(rasterB04.height)

        assertThat(rasterB05.crs).isEqualTo(rasterB04.crs)
    }
}
