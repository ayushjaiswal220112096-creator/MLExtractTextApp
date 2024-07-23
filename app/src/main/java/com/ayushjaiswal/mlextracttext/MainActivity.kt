package com.ayushjaiswal.mlextracttext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.Manifest
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {


    lateinit var result: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val camera = findViewById<ImageView>(R.id.cameraBtn)
        val erase = findViewById<ImageView>(R.id.eraseBtn)
        val copy = findViewById<ImageView>(R.id.copyBtn)
        result = findViewById<EditText>(R.id.resultTV)

        camera.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                // Permission already granted, proceed with opening the camera


                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if (intent.resolveActivity(packageManager) != null) {
                    // I want to recieve the image and send it for result extraction
                    startActivityForResult(intent, 123)
                } else {
                    // Something went wrong
                    Toast.makeText(this, "Oops! something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

        }


        erase.setOnClickListener {
            result.setText("")
        }
        copy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label",result.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this,"Copied to Clipboard",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with opening the camera
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if (intent.resolveActivity(packageManager) != null) {
                    // I want to recieve the image and send it for result extraction
                    startActivityForResult(intent, 123)
                } else {
                    // Something went wrong
                    Toast.makeText(this, "Oops! something went wrong", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Camera Permission Not Granted, Enable camera to continue...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==123 && resultCode== RESULT_OK) {
            val extras = data?.extras

            val bitmap= extras?.get("data") as Bitmap
            detectTextUsingML(bitmap)
        }
    }
    fun detectTextUsingML(bitmap: Bitmap){

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val image = InputImage.fromBitmap(bitmap,0)

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                result.setText(visionText.text.toString())
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Toast.makeText(this,"Problem with detection!",Toast.LENGTH_SHORT).show()
            }

    }
}