package data

class MultiRaster(
    private val bands: Map<String, Raster>
) {
    init {
        require(bands.isNotEmpty()) { "하나 이상의 밴드가 필요합니다." }

        val firstRaster = bands.values.first()
        require(bands.values.all {
                    it.width == firstRaster.width &&
                    it.height == firstRaster.height &&
                    it.crs == firstRaster.crs
        }) { "모든 래스터의 크기 및 좌표계가 동일해야 합니다." }
    }

    val width: Int get() = bands.values.first().width
    val height: Int get() = bands.values.first().height
    val geoTransform: GeoTransform get() = bands.values.first().geoTransform

    fun getBandNames(): Set<String> = bands.keys

    fun getBandByName(name: String): Raster{
        return bands[name]
            ?: throw IllegalArgumentException("'${name}' 이름의 밴드를 찾을 수 없습니다.")
    }
}
