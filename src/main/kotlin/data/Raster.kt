package data

data class Raster(
    val width: Int,
    val height: Int,
    val geoTransform: GeoTransform,
    val crs: String = "EPSG:4326",
    val digitalnumber: Array<FloatArray>,
    val bandIndex: Int = 1
) {

    fun getValue(x: Int, y: Int): Float?{
        if( y !in 0 until height || x !in 0 until width){
            return null
        }
        return digitalnumber[y][x]
    }

    fun setValue(x: Int, y: Int, value: Float){
        if (y in 0 until height && x in 0 until width){
            digitalnumber[y][x] = value
        }
    }

    fun map(other: Raster, op: (Float, Float) -> Float): Raster{
        require(width == other.width && height == other.height){
            "Raster 크기가 일치하지 않습니다."
        }

        val result = Array(height){y->
            FloatArray(width){x->
                op(this.digitalnumber[y][x], other.digitalnumber[y][x])
            }
        }

        return Raster(width, height, geoTransform, crs, result)
    }

    fun map(op: (Float) -> Float): Raster{
        val result = Array(height){y->
            FloatArray(width){x->
                op(this.digitalnumber[y][x])
            }
        }
        return Raster(width,height,geoTransform,crs,result)
    }
}
