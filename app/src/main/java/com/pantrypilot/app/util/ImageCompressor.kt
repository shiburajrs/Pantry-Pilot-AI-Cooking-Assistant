package com.pantrypilot.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

object ImageCompressor {

    const val DEFAULT_MAX_DIMENSION = 1024
    const val DEFAULT_JPEG_QUALITY = 80

    fun decodeSampledBitmap(
        imageFile: File,
        maxDimension: Int = DEFAULT_MAX_DIMENSION
    ): Bitmap? {
        if (!imageFile.exists() || imageFile.length() == 0L) return null

        val filePath = imageFile.absolutePath

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(filePath, options)

        val origWidth = options.outWidth
        val origHeight = options.outHeight
        if (origWidth <= 0 || origHeight <= 0) return null

        val maxOriginalDimension = max(origWidth, origHeight)
        var inSampleSize = 1
        if (maxOriginalDimension > maxDimension) {
            val halfMax = maxOriginalDimension / 2
            while ((halfMax / inSampleSize) >= maxDimension) {
                inSampleSize *= 2
            }
        }

        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val sampledBitmap = BitmapFactory.decodeFile(filePath, options) ?: return null

        val rotationDegrees = getExifRotationDegrees(filePath)

        val currentMaxDim = max(sampledBitmap.width, sampledBitmap.height)
        val scale = if (currentMaxDim > maxDimension) {
            maxDimension.toFloat() / currentMaxDim.toFloat()
        } else {
            1f
        }

        val matrix = Matrix().apply {
            if (rotationDegrees != 0) {
                postRotate(rotationDegrees.toFloat())
            }
            if (scale < 1f) {
                postScale(scale, scale)
            }
        }

        val finalBitmap = if (!matrix.isIdentity) {
            val transformed = Bitmap.createBitmap(
                sampledBitmap,
                0,
                0,
                sampledBitmap.width,
                sampledBitmap.height,
                matrix,
                true
            )
            if (transformed != sampledBitmap) {
                sampledBitmap.recycle()
            }
            transformed
        } else {
            sampledBitmap
        }

        return finalBitmap
    }

    fun compressImageFile(
        sourceFile: File,
        destinationFile: File = sourceFile,
        maxDimension: Int = DEFAULT_MAX_DIMENSION,
        quality: Int = DEFAULT_JPEG_QUALITY
    ): File {
        val bitmap = decodeSampledBitmap(sourceFile, maxDimension)
            ?: return sourceFile

        return try {
            val tempFile = if (destinationFile.absolutePath == sourceFile.absolutePath) {
                File(sourceFile.parentFile, "temp_comp_${System.currentTimeMillis()}.jpg")
            } else {
                destinationFile
            }

            FileOutputStream(tempFile).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream)
                outStream.flush()
            }

            if (tempFile.absolutePath != destinationFile.absolutePath) {
                if (destinationFile.exists()) {
                    destinationFile.delete()
                }
                tempFile.renameTo(destinationFile)
            } else if (destinationFile.absolutePath == sourceFile.absolutePath) {
                if (sourceFile.exists()) {
                    sourceFile.delete()
                }
                tempFile.renameTo(sourceFile)
            }

            destinationFile
        } catch (e: Exception) {
            sourceFile
        } finally {
            bitmap.recycle()
        }
    }

    private fun getExifRotationDegrees(filePath: String): Int {
        return try {
            val exif = ExifInterface(filePath)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }
}
