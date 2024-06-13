package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.dicoding.picodiploma.loginwithanimation.BuildConfig
import com.dicoding.picodiploma.loginwithanimation.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
const val MAX_IMAGE_SIZE = 500000L // Changed to Long
private const val INITIAL_IMAGE_WIDTH = 2048
private const val INITIAL_IMAGE_HEIGHT = 2048
private const val FINAL_IMAGE_WIDTH = 1024
private const val FINAL_IMAGE_HEIGHT = 1024
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun getImageUri(context: Context): Uri? {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyCamera")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    Log.d("getImageUri", "URI: $uri")
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "MyCamera/$timeStamp.jpg")
    if (!imageFile.parentFile.exists()) imageFile.parentFile.mkdirs()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun uploadImage(currentImageUri: Uri?, context: Context) {
    Log.d("uploadImage", "Attempting to upload image...")
    currentImageUri?.let { uri ->
        val imageFile = uriToFile(uri, context).reduceFileSize(MAX_IMAGE_SIZE)
    } ?: showToast(context.getString(R.string.empty_image_warning), context)
}

private fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun uriToFile(uri: Uri, context: Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "image_temp.jpg")
    inputStream?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file
}

fun Context.getString(resourceId: Int): String {
    return resources.getString(resourceId)
}

fun File.reduceFileSize(maxSize: Long): File {
    var compressQuality = 100
    var streamLength: Int
    var byteArray: ByteArray // Declare byteArray here
    do {
        val bmp = BitmapFactory.decodeFile(this.path)
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, compressQuality, stream)
        byteArray = stream.toByteArray()
        streamLength = byteArray.size
        compressQuality -= 5
    } while (streamLength > maxSize && compressQuality > 0)

    try {
        val outputStream = FileOutputStream(this)
        outputStream.write(byteArray)
        outputStream.flush()
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return this
}
