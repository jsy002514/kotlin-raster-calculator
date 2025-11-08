package data

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RasterTest {

    @Test
    fun `DN_평균이_올바르게_계산되어야_한다`(){
        val data = arrayOf(
            floatArrayOf(1f, 2f, 3f),
            floatArrayOf(4f, 5f, 6f)
        )

        val raster = Raster(
            width = 3,
            height = 2,
            geoTransform = GeoTransform(0.0, 1.0, 0.0, 0.0, 0.0, -1.0),
            data = data
        )

        assertThat (raster.meanDN()).isEqualTo(3.5f)
    }

    @Test
    fun `DN_최소와_최대가_정확해야_한다`(){
        val data = arrayOf(
            floatArrayOf(10f, 5f),
            floatArrayOf(15f, 20f)
        )

        val raster = Raster(2, 2, GeoTransform(0.0,1.0,0.0,0.0,0.0,-1.0), data)

        assertThat(raster.minDN()).isEqualTo(5f)
        assertThat(raster.maxDN()).isEqualTo(20f)
    }
}
