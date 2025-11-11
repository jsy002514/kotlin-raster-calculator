package data

data class Raster(
    val width: Int,
    val height: Int,
    val geoTransform: GeoTransform,
    val crs: String = "EPSG:4326",
    private val data: RasterData
) {
    constructor(
        width: Int,
        height: Int,
        geoTransform: GeoTransform,
        crs: String = "EPSG:4326",
        values: List<Float>
    ) : this(
        width,
        height,
        geoTransform,
        crs,
        RasterData(values, width, height)
    )

    init {
        require(width > 0 && height > 0){"래스터 크기(width, height)는 0보다 커야 합니다."}
    }

    fun getValue(x: Int, y: Int): Float = data.getValue(x, y)

    fun meanDN(): Float = data.mean()
    fun minDN(): Float = data.min()
    fun maxDN(): Float = data.max()

    private fun map(op: (Float) -> Float): Raster{
        val newData = data.map(op)
        return Raster(width, height, geoTransform, crs, newData)
    }

    private fun map(other: Raster, op: (Float, Float)-> Float): Raster{
        require(width == other.width && height == other.height){
            "연산하려는 두 래스터의 크기가 다릅니다. (this: ${width}x${height}, other: ${other.width}x${other.height})"
        }
        val newData = data.map(other.data, op)
        return Raster(width, height, geoTransform, crs, newData)
    }

    operator fun plus(other: Raster): Raster = map(other){a,b->a+b}
    operator fun plus(value:Float): Raster = map{it+value}

    operator fun minus(other: Raster): Raster = map(other){a,b->a-b}
    operator fun minus(value:Float): Raster = map{it-value}

    operator fun times(other: Raster): Raster = map(other){a,b->a*b}
    operator fun times(value:Float): Raster = map{it*value}

    operator fun div(other: Raster): Raster = map(other){a,b ->
        if(b==0f){
            0f
        }else{
            a/b
        }
    }
    operator fun div(value: Float): Raster = map{
        if (value == 0f){
            0f
        }else{
            it / value
        }
    }
}
