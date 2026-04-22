package com.example.displayimageapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imageButton = findViewById<ImageButton>(R.id.addImageButton)
        var imageExists = false

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                selectedImageUri = uri
                val imageView = findViewById<ImageView>(R.id.displayXRay)
                imageView.setImageURI(uri)
                imageExists = true
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        // Launch the photo picker and let the user choose only images.
        imageButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val processButton = findViewById<ImageButton>(R.id.processButton)
        val results = findViewById<TextView>(R.id.results)

        processButton.setOnClickListener {
            val uri = selectedImageUri

            if (uri == null) {
                results.setText("No Image")
                return@setOnClickListener
            }

            results.setText("Uploading and processing...")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val imageBytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (imageBytes == null) {
                        withContext(Dispatchers.Main) {
                            results.setText("Could not read image.")
                        }
                        return@launch
                    }

                    val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                    val requestBody = imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())

                    val multipartBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "xray.jpg", requestBody)
                        .build()

                    val request = Request.Builder()
                        .url("https://pneuvisionai.com/predict")
                        .post(multipartBody)
                        .build()

                    val response = client.newCall(request).execute()
                    val bodyString = response.body?.string()
                    Log.d("SERVER_RESPONSE", bodyString ?: "null")

                    if (!response.isSuccessful || bodyString == null) {
                        withContext(Dispatchers.Main) {
                            results.setText("Server error: ${response.code}")
                        }
                        return@launch
                    }

                    val json = JSONObject(bodyString)
                    val prediction = json.optString("class_name", "Unknown")
                    val confidence = json.optDouble("confidence", 0.0)

                    withContext(Dispatchers.Main) {
                        results.setText(
                            "Results: $prediction\nConfidence: ${String.format("%.2f%%", confidence * 100)}"
                        )
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        results.setText("Error: ${e.message}")
                    }
                }
            }
        }


    }
}