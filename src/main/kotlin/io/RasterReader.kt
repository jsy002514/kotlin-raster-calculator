package io
import data.MultiRaster
import java.io.File

interface RasterReader {
    fun read(file: File): MultiRaster
}
