package com.bazumax.capacitor.firebase.storage

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min
import android.util.Base64


suspend fun comressImage(uri: Uri, activity: Activity): Uri {
    // Get filename
    val name = Base64.encodeToString(uri.encodedPath!!.toByteArray(), Base64.DEFAULT)
    val copyFile = File(activity.applicationContext.cacheDir, name)

    val inputStream = activity.contentResolver.openInputStream(uri)
    val outputStream = FileOutputStream(copyFile)
    var read = 0
    val maxBufferSize = 1024 * 1024
    val bufferSize = min(inputStream!!.available(), maxBufferSize)
    val buffers = ByteArray(bufferSize)
    while (inputStream.read(buffers).also { read = it } != -1) {
        outputStream.write(buffers, 0, read)
    }
    inputStream.close()
    outputStream.close()

    val compressedFile = Compressor.compress(activity.applicationContext, copyFile) {
        quality(100)
        format(Bitmap.CompressFormat.JPEG)
        size(2_097_152)
    }

    return compressedFile.toUri()
}