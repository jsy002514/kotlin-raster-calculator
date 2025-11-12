package io

import data.MultiRaster
import data.GeoTransform
import data.Raster
import java.io.File
import java.awt.image.Raster as JaiRaster
import org.geotools.gce.geotiff.GeoTiffReader

class GeoTiffReader : RasterReader {
    override fun read(file: File): MultiRaster {
        GeoTiffReader(file).use{
            reader ->
            val commonInfo = readCommonInfo(reader)
            val bandMap = createBandMap(reader, commonInfo)
            return MultiRaster(bandMap)
        }
    }

    private fun readCommonInfo(reader: GeoTiffReader): RasterCommonInfo{
        val gridRange = reader.originalGridRange
        val width = gridRange.getSpan(0)
        val height = gridRange.getSpan(1)
        val envelope = reader.originalEnvelope
        val originX = envelope.minX
        val originY = envelope.maxY
        val pixelSizeX = envelope.width / width
        val pixelSizeY = -(envelope.height / height)
        val geoTransform = GeoTransform(originX, originY, pixelSizeX, pixelSizeY)
        val crs = reader.coordinateReferenceSystem.toWKT()
        val bandCount = reader.gridCoverageCount

        return RasterCommonInfo(width, height, geoTransform, crs, bandCount)
    }

    private fun createBandMap(reader: GeoTiffReader, commonInfo:RasterCommonInfo):Map<String, Raster>{
        val bandMap = mutableMapOf<String, Raster>()

        for(i in 0 until commonInfo.bandCount){
            val raster = readSingleBand(reader, i, commonInfo)
            val bandName = "b${i+1}"
            bandMap[bandName] = raster
        }
        return bandMap
    }
}
