package com.miraz.misattendance

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
object FileUtils {
    @Throws(IOException::class)
    fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        // Create a file in the cache directory
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        return file
    }
}
