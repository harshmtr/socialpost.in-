package com.example.socialpost.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

object SharingUtils {
    private val client = OkHttpClient()

    suspend fun sharePost(context: Context, text: String, imageUrl: String?) {
        // 1. Copy text to clipboard as a fallback (LinkedIn often ignores text when image is shared)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("LinkedIn Post", text)
        clipboard.setPrimaryClip(clip)

        withContext(Dispatchers.IO) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = if (imageUrl != null) "image/jpeg" else "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)

                if (imageUrl != null) {
                    try {
                        val imageFile = downloadImage(context, imageUrl)
                        if (imageFile != null) {
                            val imageUri: Uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                imageFile
                            )
                            putExtra(Intent.EXTRA_STREAM, imageUri)
                            // Set clipData for better compatibility and URI permission granting
                            clipData = ClipData.newRawUri("Post Image", imageUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        type = "text/plain" // Fallback to text only
                    }
                }
            }
            
            val chooser = Intent.createChooser(shareIntent, "Share to LinkedIn")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }

    private fun downloadImage(context: Context, url: String): File? {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        
        if (!response.isSuccessful) return null
        
        val sharedImagesDir = File(context.cacheDir, "shared_images")
        if (!sharedImagesDir.exists()) sharedImagesDir.mkdirs()
        
        val file = File(sharedImagesDir, "post_image_${System.currentTimeMillis()}.jpg")
        
        response.body?.byteStream()?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        
        return file
    }
}
