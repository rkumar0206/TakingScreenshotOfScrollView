package com.rohitthebest.screenshot_of_scrollview

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rohitthebest.screenshot_of_scrollview.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.takeScreenShotBtn.setOnClickListener {

            val bitmap = loadBitmapFromView(
                binding.linearLayout,
                binding.linearLayout.width,
                binding.linearLayout.height
            )

            //check for 'read and write storage permission' first, then call this method
            saveBitmap(bitmap)
        }
    }

    private fun loadBitmapFromView(view: View, width: Int, height: Int): Bitmap {

        val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        view.draw(canvas)

        return bitmap
    }

    private fun saveBitmap(bitmap: Bitmap) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val resolver = this.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "screenshot.jpeg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DCIM}/LongScreenshot"
                )
            }

            val uri = resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                resolver.openOutputStream(it).use { fout ->
                    try {

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout)
                        fout?.close()

                        Toast.makeText(
                            applicationContext,
                            "Image Saved in DCIM/LongScreenshot",
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {

            try {
                val storageDir: File =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/LongScreenshot")

                var success = true
                if (!storageDir.exists()) {
                    success = storageDir.mkdirs()
                }

                if (success) {

                    val imageFile = File(storageDir, "screenshot.jpeg")
                    //savedImagePath = imageFile.absolutePath
                    try {
                        val fOut: OutputStream = FileOutputStream(imageFile)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                        fOut.close()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                    Toast.makeText(
                        applicationContext,
                        "Image Saved in DCIM/LongScreenshot",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            } catch (e: Exception) {

                e.printStackTrace()
            }

        }
    }
}