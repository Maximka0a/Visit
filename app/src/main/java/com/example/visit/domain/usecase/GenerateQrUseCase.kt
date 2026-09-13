package com.example.visit.domain.usecase

import android.graphics.Bitmap
import com.example.visit.domain.model.Profile
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.serialization.json.Json
import androidx.core.graphics.createBitmap
import android.graphics.Color
import androidx.core.graphics.set
import javax.inject.Inject

class GenerateQrUseCase @Inject constructor() {
    operator fun invoke(profile: Profile): Bitmap {
        val width = 512
        val height = 512

        val json = Json.encodeToString(profile)
        val writer = MultiFormatWriter()

        val matrix = writer.encode(json, BarcodeFormat.QR_CODE, width, height)

        val bitmap = createBitmap(width, height)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (matrix.get(x, y)) Color.BLACK else Color.WHITE
                bitmap[x, y] = color
            }
        }
        return bitmap
    }
}
