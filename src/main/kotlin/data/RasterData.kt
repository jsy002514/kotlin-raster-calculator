package data

class RasterData(
    val values:List<Float>,
    val width: Int,
    val height: Int
) {
    init{
        require(values.size == width * height){
            "데이터 크기(${values.size})가 래스터 크기(${width}x${height} = ${width*height})와 일치하지 않습니다."
        }
    }

    fun getValue(x: Int, y:Int): Float{
        require(y in 0 until height && x in 0 until width){
            "($x, $y) 좌표가 래스터 범위(WxH: $width x $height)를 벗어났습니다."
        }
        return values[y*width + x]
    }

    fun map(other: RasterData, op: (Float, Float) -> Float): RasterData{
        val result = values.zip(other.values, op)
        return RasterData(result, width, height)
    }

    fun mean(): Float = values.average().toFloat()

    fun min(): Float = values.minOrNull() ?: Float.NaN

    fun max(): Float = values.maxOrNull() ?: Float.NaN
}
