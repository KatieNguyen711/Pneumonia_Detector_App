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


class MainActivity : AppCompatActivity() {

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
        val results = findViewById<EditText>(R.id.results)
        //After an image is added, click this and the results will show
        processButton.setOnClickListener {
            if(imageExists){
                results.setText("Results: \nConfidence:")
            }else{
                results.setText("No Image")
            }
        }


    }
}