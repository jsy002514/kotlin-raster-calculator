package io

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class Sentinel2Jp2ReaderTest {

    @Test
    fun `Sentinel-2 폴더를_읽고_리샘플링해야_한다`() {

        val reader: RasterReader = Sentinel2Jp2Reader(targetResolution = "R10m")

        val resourceDir = File(ClassLoader.getSystemResource("sentinel_test_data/IMG_DATA").toURI())

        val multiRaster = reader.read(resourceDir)

        assertThat(multiRaster.getBandNames()).contains("B04", "B03", "B08A") // (10m, 20m, 60m 밴드 예시)

        val rasterB04 = multiRaster.getBandByName("B04") // 10m
        val rasterB03 = multiRaster.getBandByName("B03") // 20m -> 10m
        val rasterB8A = multiRaster.getBandByName("B8A") // 60m -> 10m

        assertThat(rasterB03.width).isEqualTo(rasterB04.width)
        assertThat(rasterB03.height).isEqualTo(rasterB04.height)

        assertThat(rasterB8A.width).isEqualTo(rasterB04.width)
        assertThat(rasterB8A.height).isEqualTo(rasterB04.height)

        assertThat(rasterB04.crs).isEqualTo(rasterB04.crs)
    }
}
