package io

import org.junit.jupiter.api.Test
import java.io.File

class GeoTiffReaderTest {

    @Test
    fun `GeoTIFF_파일을_정상적으로_읽어야_한다`(){
        val reader = GeoTiffReader()
        val raster = reader.read(File("src/test/resources/sample.tif"))

        assertThat(raster.crs).isEqualTo("EPSG:4326")
        assertThat(raster.width).isGreaterThan(0)
        assertThat(raster.height).isGreaterThan(0)
    }
}
