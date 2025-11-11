package data

data class GeoTransform(
    val originX: Double,
    val originY: Double,
    val pixelSizeX: Double,
    val pixelSizeY: Double = -pixelSizeX
    val rotationX: Double = 0.0,
    val rotationY: Double = 0.0
)
