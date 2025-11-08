package data

data class MultiRaster(
    val rasters: List<Raster>
) {
    init {
        require(rasters.isNotEmpty()) { "하나 이상의 래스터가 필요합니다." }

        val first = rasters.first()
        require(rasters.all {
            it.width == first.width &&
                    it.height == first.height &&
                    it.crs == first.crs
        }) { "모든 래스터의 크기 및 좌표계가 동일해야 합니다." }
    }

    val width: Int get() = rasters.first().width
    val height: Int get() = rasters.first().height
    val geoTransform: GeoTransform get() = rasters.first().geoTransform
    val crs: String get() = rasters.first().crs

    fun getBand(index: Int): Raster? = rasters.getOrNull(index - 1)
    fun addBand(raster: Raster): MultiRaster {
        return MultiRaster(rasters + raster)
    }

    fun toSingleBand(index: Int): Raster {
        return getBand(index)
            ?: throw IllegalArgumentException("해당 인덱스($index)의 밴드가 없습니다.")
    }
}
