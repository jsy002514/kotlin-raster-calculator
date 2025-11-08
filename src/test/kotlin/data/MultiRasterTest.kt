package data

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class MultiRasterTest {

    @Test
    fun `NDVI_계산_결과가_-1과_1_사이여야_한다`(){
        val red = Raster(
            2, 2,
            GeoTransform(0.0, 1.0, 0.0, 0.0, 0.0, -1.0),
            arrayOf(
                floatArrayOf(0.2f, 0.3f),
                floatArrayOf(0.4f, 0.5f)
            )
        )

        val nir = Raster(
            GeoTransform(0.0, 1.0, 0.0, 0.0, 0.0, -1.0),
            arrayOf(
                floatArrayOf(0.6f, 0.7f),
                floatArrayOf(0.8f, 0.9f)
            )

        )

        val multi = MultiRaster(mapOf("red" to red, "nir" to nir))
        val ndvi = multi.calculateNDVI()

        ndvi.data.forEach{ row ->
            row.forEach{ value ->
                assertThat(value).isBetween(-1.0f, 1.0f)
            }
        }
    }
}
