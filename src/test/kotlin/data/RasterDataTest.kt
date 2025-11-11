package data

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

class RasterDataTest {

    private lateinit var baseGeoTransform: GeoTransform

    @BeforeEach
    fun setUp(){
        baseGeoTransform = GeoTransform(0.0, 0.0, 1.0)
    }

    @Test
    fun `래스터_생성_시_데이터_크기가_다르면_예외가_발생한다`(){
        val wrongData = listOf(1f,2f,3f)

        assertThatThrownBy {
            RasterData(wrongData, 2, 2)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("데이터 크기(3)가 래스터 크기(2x2 = 4)와 일치하지 않습니다.")
    }

    @Test
    fun`getValue가_좌표_범위를_벗어나면_예외가_발생한다`(){
        val data = RasterData(listOf(1f, 2f, 3f, 4f), 2, 2)

        assertThatThrownBy {
            data.getValue(2,2)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("좌표가 래스터 범위(WxH: 2 x 2)를 벗어났습니다.")
    }

    @Test
    fun`getValue가_정확한_인덱스_값을_반환한다`(){
        val data = RasterData(listOf(1f,2f,3f,4f),2,2)
        assertThat(data.getValue(1,0)).isEqualTo(2f)
        assertThat(data.getValue(0,1)).isEqualTo(3f)
    }
}
