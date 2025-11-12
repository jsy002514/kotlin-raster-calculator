package io

import data.GeoTransform
import data.MultiRaster
import data.Raster
import org.geotools.coverage.grid.GridCoverage2D
import org.geotools.gce.geotiff.GeoTiffReader
import org.geotools.referencing.CRS // CRS.toWKT() 사용
import org.geotools.util.factory.Hints
import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import java.io.File

import java.awt.image.Raster as JaiRaster
import org.geotools.geometry.jts.ReferencedEnvelope
import org.opengis.geometry.Envelope
import org.geotools.referencing.ReferencingFactoryFinder
import org.geotools.api.parameter.GeneralParameterValue


class GeoTiffReader : RasterReader {

    private val hints = Hints(Hints.LENIENT_DATUM_SHIFT, true)

    // read, processFolder, processSingleFile, parseBandName, extractPixels은 이전과 동일한 로직 유지

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

            val generalBounds = reader.originalEnvelope
            val crs = reader.coordinateReferenceSystem

            // ⭐️ [안전한 방법] ReferencedEnvelope 생성
            val referencedEnvelope = ReferencedEnvelope(
                generalBounds.getMinimum(0),
                generalBounds.getMaximum(0),
                generalBounds.getMinimum(1),
                generalBounds.getMaximum(1),
                crs
            )

            val originX = referencedEnvelope.minX
            val originY = referencedEnvelope.maxY
            val pixelSizeX = referencedEnvelope.width / width
            val pixelSizeY = -(referencedEnvelope.height / height)
            val geoTransform = GeoTransform(originX, originY, pixelSizeX, pixelSizeY)

            val bandCount = reader.gridCoverageCount

            return RasterCommonInfo(width, height, geoTransform, crs, bandCount)
        } finally {
            reader.dispose()
        }
    }

    // ⭐️ [이것이 핵심 수정] ⭐️
    // 밴드를 읽기 위해 복잡한 파라미터 대신 'read(bandIndex)' 오버로드를 사용
    private fun readSingleBandTiff(file: File): GridCoverage2D {
        val reader = GeoTiffReader(file, hints)
        try {
            // 핵심: *emptyArray<GeneralParameterValue>()
            return reader.read(*emptyArray<GeneralParameterValue>())
                ?: throw IllegalStateException("TIF 읽기 실패: ${file.name}")
        } finally {
            reader.dispose()
        }
    }

    private fun processFolder(folder: File, baseInfo: RasterCommonInfo, bandMap: MutableMap<String, Raster>) {
        val tiffFiles = folder.listFiles { _, name -> name.endsWith(".tiff") || name.endsWith(".tif") } ?: return
        for (file in tiffFiles) {
            processSingleFile(file, baseInfo, bandMap)
        }
    }

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
            baseInfo.crs.toWKT(),
            pixels
        )
        bandMap[bandName] = raster
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

        // 방법 1: 전체 이미지를 한 번에 읽기 (권장)
        val raster: java.awt.image.Raster = image.data
        raster.getSamples(0, 0, width, height, 0, data)

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
