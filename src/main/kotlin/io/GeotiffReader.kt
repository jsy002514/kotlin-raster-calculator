package io

import data.GeoTransform
import data.MultiRaster
import data.Raster
import org.geotools.coverage.grid.GridCoverage2D
import org.geotools.gce.geotiff.GeoTiffReader
import org.geotools.referencing.CRS
import org.geotools.util.factory.Hints
import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import java.io.File
import org.geotools.api.parameter.GeneralParameterValue
import java.awt.image.Raster as JaiRaster
// ⭐️ [이것이 핵심 1] ⭐️
import org.geotools.geometry.jts.ReferencedEnvelope // 1. Envelope 클래스 임포트
import org.opengis.geometry.Envelope // 2. (GeoTools 30.1 호환성을 위해)

class GeoTiffReader : RasterReader {

    private val hints = Hints(Hints.LENIENT_DATUM_SHIFT, true)

    // ... (read 메서드는 이전과 동일) ...
    override fun read(directory: File): MultiRaster {
        val tiffFiles = directory.listFiles { _, name -> name.endsWith(".tiff") || name.endsWith(".tif") }
            ?: throw IllegalArgumentException("폴더를 찾을 수 없거나 .tiff 파일이 없습니다: ${directory.path}")

        require(tiffFiles.isNotEmpty()) { "폴더에 .tiff 파일이 없습니다: ${directory.path}" }

        val baseInfo = readCommonInfo(tiffFiles.first())
        val bandMap = mutableMapOf<String, Raster>()

        for (file in tiffFiles) {
            processSingleFile(file, baseInfo, bandMap)
        }

        return MultiRaster(bandMap)
    }


    private fun readCommonInfo(baseFile: File): RasterCommonInfo {
        val reader = GeoTiffReader(baseFile, hints)
        try {
            val gridRange = reader.originalGridRange
            val width = gridRange.getSpan(0)
            val height = gridRange.getSpan(1)

            // ⭐️ [이것이 핵심 2] ⭐️
            // 'reader.originalEnvelope'를 'ReferencedEnvelope' 타입으로 명시합니다.
            val envelope: ReferencedEnvelope = reader.originalEnvelope
            val crs = reader.coordinateReferenceSystem

            // 'envelope'가 명확한 타입을 가지므로 .minX, .maxY 등을 찾을 수 있습니다.
            val originX = envelope.minX
            val originY = envelope.maxY
            val pixelSizeX = envelope.width / width
            val pixelSizeY = -(envelope.height / height)
            val geoTransform = GeoTransform(originX, originY, pixelSizeX, pixelSizeY)

            val bandCount = reader.gridCoverageCount

            return RasterCommonInfo(width, height, geoTransform, crs, bandCount)
        } finally {
            reader.dispose()
        }
    }

    // ... (processSingleFile, readSingleBandTiff, parseBandName, extractPixels, RasterCommonInfo ... 는 이전과 동일) ...
    private fun processSingleFile(
        file: File,
        baseInfo: RasterCommonInfo,
        bandMap: MutableMap<String, Raster>
    ) {
        val coverage = readSingleBandTiff(file)
        val pixels = extractPixels(coverage)
        val bandName = parseBandName(file.name)

        val raster = Raster(
            baseInfo.width,
            baseInfo.height,
            baseInfo.geoTransform,
            CRS.toWKT(baseInfo.crs),
            pixels
        )
        bandMap[bandName] = raster
    }

    private fun readSingleBandTiff(file: File): GridCoverage2D {
        val reader = GeoTiffReader(file, hints)
        try {
            val params = reader.getReadParameters()
            params.parameter("BANDS").setValue(intArrayOf(0))

            return reader.read(params)
                ?: throw IllegalStateException("TIF 읽기 실패: ${file.name}")
        } finally {
            reader.dispose()
        }
    }

    private fun parseBandName(fileName: String): String {
        val nameWithoutSuffix = fileName.substringBeforeLast(".").substringBeforeLast("(")
        val parts = nameWithoutSuffix.split("_")
        return parts.getOrNull(parts.size - 2)
            ?: throw IllegalArgumentException("밴드 이름 파싱 실패: $fileName")
    }

    private fun extractPixels(coverage: GridCoverage2D): List<Float> {
        val image = coverage.renderedImage
        val width = image.width
        val height = image.height
        val data = FloatArray(width * height)
        val tile: JaiRaster = image.getTile(0, 0)

        tile.getSamples(0, 0, width, height, 0, data)
        return data.toList()
    }

    private data class RasterCommonInfo(
        val width: Int,
        val height: Int,
        val geoTransform: GeoTransform,
        val crs: CoordinateReferenceSystem,
        val bandCount: Int
    )
}
