package data

data class Raster(
    val width: Int,
    val height: Int,
    val geoTransform: GeoTransform,
    val crs: String = "EPSG:4326",
    val data: List<Float>,
) {
    init {
        require(data.size == width * height){
            "데이터 크기(${data.size})가 래스터 크기(${width}x${height} = ${width*height})와 일치하지 않습니다."
        }
    }

    fun getValue(x: Int, y: Int): Float?{
        if( y !in 0 until height || x !in 0 until width){
            return null
        }
        return data[y*width + x]
    }

    fun map(other: Raster, op: (Float, Float) -> Float): Raster{
        require(width == other.width && height == other.height){
            "Raster 크기가 일치하지 않습니다."
        }

        val result = data.zip(other.data, op)
        return this.copy(data = result)
    }

    fun map(op: (Float) -> Float): Raster{
        val result = data.map(op)
        return this.copy(data = result)
    }
}
