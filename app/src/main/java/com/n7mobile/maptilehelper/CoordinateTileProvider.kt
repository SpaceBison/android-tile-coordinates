package com.n7mobile.maptilehelper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import java.io.ByteArrayOutputStream

class CoordinateTileProvider(val width: Int = 512, val height: Int = 512) : TileProvider {
    private val cache = HashMap<TileCoordinates, Tile>()
    private val borderPaint = Paint().apply {
        color = 0xff000000.toInt()
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint().apply {
        color = 0xff000000.toInt()
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 32f
    }

    override fun getTile(x: Int, y: Int, zoom: Int): Tile =
        TileCoordinates(x, y, zoom).let {
            cache.getOrPut(it) { generateTile(it) }
        }

    fun clearCache() = cache.clear()

    private fun generateTile(tileCoordinates: TileCoordinates): Tile =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    .also {
                        Canvas(it).apply {
                            drawARGB(0, 0, 0, 0)
                            drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)
                            drawText("(${tileCoordinates.x}, ${tileCoordinates.y}, ${tileCoordinates.z})",
                                    width.toFloat() / 2, height.toFloat() / 2, textPaint)
                        }
                    }
                    .run {
                        ByteArrayOutputStream().use {
                            compress(Bitmap.CompressFormat.PNG, 50, it)
                            recycle()
                            it.toByteArray()
                        }
                    }
                    .let { Tile(width, height, it) }

    data class TileCoordinates(val x: Int, val y: Int, val z: Int)
}