package io

import data.GeoTransform
import data.MultiRaster
import data.Raster
import org.geotools.coverage.grid.GridCoverage2D
import org.geotools.coverage.grid.GridGeometry2D
import org.geotools.coverage.processing.Operations
import org.geotools.gce.geotiff.GeoTiffReader
import org.geotools.geometry.jts.ReferencedEnvelope
import org.geotools.referencing.CRS
import org.geotools.util.factory.Hints
import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import java.io.File
import javax.media.jai.Interpolation
import org.geotools.coverage.processing.operation.Interpolate

class Sentinel2Jp2Reader(
    private val targetResolution: String = "R10m"
) : RasterReader {

    private val hints = Hints(Hints.LENIENT_DATUM_SHIFT, true)
    private val operations = Operations(hints)

    override fun read(directory: File): MultiRaster {
        val baseFolder = File(directory, targetResolution)
        val baseInfo = findBaseMetadata(baseFolder)

        val bandMap = mutableMapOf<String, Raster>()

        processFolder(File(directory, "R10m"), baseInfo, bandMap)
        processFolder(File(directory, "R20m"), baseInfo, bandMap)
        processFolder(File(directory, "R60m"), baseInfo, bandMap)

        return MultiRaster(bandMap)
    }

    private fun findBaseMetadata(baseFolder: File): RasterCommonInfo {
        val baseFile = baseFolder.listFiles { _, name -> name.endsWith(".jp2") }
            ?.firstOrNull()
            ?: throw IllegalArgumentException("기준 폴더(${baseFolder.path})에 .jp2 파일이 없습니다.")

        val baseCoverage = readJp2Coverage(baseFile)
        val gridGeometry = baseCoverage.gridGeometry
        val envelope: ReferencedEnvelope = baseCoverage.envelope as ReferencedEnvelope
        val crs = gridGeometry.coordinateReferenceSystem

        val width = gridGeometry.gridRange.high.coordinateValues[0] + 1
        val height = gridGeometry.gridRange.high.coordinateValues[1] + 1

        val originX = envelope.minX
        val originY = envelope.maxY
        val pixelSizeX = envelope.width / width
        val pixelSizeY = -envelope.height / height

        val geoTransform = GeoTransform(originX, originY, pixelSizeX, pixelSizeY)

        return RasterCommonInfo(width, height, geoTransform, crs, gridGeometry)
    }

    private fun readJp2Coverage(file: File): GridCoverage2D {
        val reader = GeoTiffReader(file, hints)
        try {
            return reader.read(null) ?: throw IllegalStateException("JP2 읽기 실패: ${file.name}")
        } finally {
            reader.dispose()  // GeoTiffReader는 dispose() 사용
        }
    }

    private fun processFolder(folder: File, baseInfo: RasterCommonInfo, bandMap: MutableMap<String, Raster>) {
        val jp2Files = folder.listFiles { _, name -> name.endsWith(".jp2") } ?: return
        for (file in jp2Files) {
            processSingleFile(file, baseInfo, bandMap)
        }
    }

    private fun processSingleFile(file: File, baseInfo: RasterCommonInfo, bandMap: MutableMap<String, Raster>) {
        val coverage = readJp2Coverage(file)
        val finalCoverage = resampleIfNeeded(coverage, baseInfo)
        val pixels = extractPixels(finalCoverage)
        val bandName = parseBandName(file.name)

        val raster = Raster(
            width = baseInfo.width,
            height = baseInfo.height,
            geoTransform = baseInfo.geoTransform,
            // [수정 1] crs.toWKT() -> CRS.toWKT(crs)
            crs = baseInfo.crs.toWKT(),
            // [수정 2] data = pixels -> values = pixels
            values = pixels
        )
        bandMap[bandName] = raster
    }

    private fun parseBandName(fileName: String): String {
        val name = fileName.removeSuffix(".jp2")
        val parts = name.split("_")
        return parts.getOrNull(parts.size - 2)
            ?: throw IllegalArgumentException("밴드 이름 파싱 실패: $fileName")
    }

    private fun resampleIfNeeded(coverage: GridCoverage2D, baseInfo: RasterCommonInfo): GridCoverage2D {
        val sourceWidth = coverage.gridGeometry.gridRange.high.coordinateValues[0] + 1
        return if (sourceWidth == baseInfo.width) {
            coverage
        } else {
            resample(coverage, baseInfo.gridGeometry, baseInfo.crs)
        }
    }

    private fun resample(
        coverage: GridCoverage2D,
        targetGridGeometry: GridGeometry2D,
        targetCRS: CoordinateReferenceSystem // ⭐️ [수정 2] targetCRS 파라미터 추가
    ): GridCoverage2D {
        val interpolation = Interpolation.getInstance(Interpolation.INTERP_BILINEAR)

        return operations.resample(
            coverage,
            targetCRS,
            targetGridGeometry,
            interpolation
        ) as GridCoverage2D
    }

    private fun extractPixels(coverage: GridCoverage2D): List<Float> {
        val image = coverage.renderedImage
        val width = image.width
        val height = image.height
        val data = FloatArray(width * height)
        val tile = image.getTile(0, 0)
        tile.getSamples(0, 0, width, height, 0, data)
        return data.toList()
    }

    private data class RasterCommonInfo(
        val width: Int,
        val height: Int,
        val geoTransform: GeoTransform,
        val crs: CoordinateReferenceSystem,
        val gridGeometry: GridGeometry2D
    )
}
