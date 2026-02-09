package com.example.displayimageapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.click_button)
        val counter = findViewById<TextView>(R.id.counter)

        // initialize counter text
        counter.text = "Count: $count"

        button.setOnClickListener {
            count++
            counter.text = "Count: $count"

        }
    }/*
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<ImageButton>(R.id.addImageButton)
        val display = findViewById<ImageView>(R.id.displayXRay)

        button.setOnClickListener{
            display.setImageResource(R.drawable.xray)
        }


    }*/
}