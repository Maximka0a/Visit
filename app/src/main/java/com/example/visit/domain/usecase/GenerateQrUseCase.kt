package com.example.visit.domain.usecase

import android.graphics.Bitmap
import com.example.visit.domain.model.Profile
import com.example.visit.domain.model.ProfileQrPayload
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.serialization.json.Json
import androidx.core.graphics.createBitmap
import android.graphics.Color
import javax.inject.Inject

class GenerateQrUseCase @Inject constructor() {
    operator fun invoke(profile: Profile): Bitmap {
        val width = 512
        val height = 512

        val payload = ProfileQrPayload(
            name = profile.name,
            title = profile.title,
            tags = profile.tags,
            socialLinks = profile.socialLinks
        )
        val json = Json.encodeToString(payload)
        val writer = MultiFormatWriter()

        val matrix = writer.encode(json, BarcodeFormat.QR_CODE, width, height)

        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (matrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        val bitmap = createBitmap(width, height)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}
