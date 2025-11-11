package domain

import data.MultiRaster
import data.Raster
import java.util.Stack


class RasterCalculator {

    fun execute(postfix: List<String>, bands: MultiRaster): Raster {
        val stack = Stack<Raster>()

        for (token in postfix) {
            processToken(token, bands, stack)
        }

        require(stack.size == 1) { "수식 실행 결과가 올바르지 않습니다." }
        return stack.pop()
    }

    private fun processToken(
        token: String,
        bands: MultiRaster,
        stack: Stack<Raster>
    ) {
        if (bands.getBandNames().contains(token)) {
            stack.push(bands.getBandByName(token))
            return
        }
        if (token.toFloatOrNull() != null) {
            stack.push(createScalarRaster(token.toFloat(), bands))
            return
        }

        val operation = getBinaryOperation(token)
        if (operation != null) {
            applyBinaryOperation(stack, operation)
            return
        }

        throw IllegalArgumentException("알 수 없는 토큰: ${token}")
    }

    private fun getBinaryOperation(token: String): ((Raster, Raster) -> Raster)? {
        return when (token) {
            "+" -> { a, b -> a + b }
            "-" -> { a, b -> a - b }
            "*" -> { a, b -> a * b }
            "/" -> { a, b -> a / b }
            else -> null
        }
    }

    private fun applyBinaryOperation(
        stack: Stack<Raster>,
        op: (a: Raster, b: Raster) -> Raster
    ) {
        val b = stack.pop()
        val a = stack.pop()
        stack.push(op(a, b))
    }

    private fun createScalarRaster(value: Float, metadataSource: MultiRaster): Raster {
        val width = metadataSource.width
        val height = metadataSource.height
        val size = width * height
        val values = List(size) { value }

        return Raster(
            width = width,
            height = height,
            geoTransform = metadataSource.geoTransform,
            crs = metadataSource.crs,
            values = values
        )
    }
}

