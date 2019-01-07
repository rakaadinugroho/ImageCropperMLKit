package com.rakaadinugroho.imagefacecroper

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.google.gson.Gson
import java.io.File


class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQ_CODE = 200
    }
    lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_take_camera.setOnClickListener {
            if (PermissionUtils.requestPermission(this, 201, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val intent = Intent(this, CameraActivity::class.java)
                startActivityForResult(intent, REQ_CODE)
            }
        }

        recognize.setOnClickListener {
            recognizeImage()
        }
    }

    private fun recognizeImage() {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(File(imagePath)))
        val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()

        val image = FirebaseVisionImage.fromFilePath(this, Uri.fromFile(File(imagePath)))
        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(highAccuracyOpts)
        detector.detectInImage(image)
            .addOnSuccessListener {
                it.forEach { face ->
                    Log.e("posisi", Gson().toJson(face))
                    Log.e("posisi-x", face.boundingBox.centerX().toString())
                    Log.e("posisi-y", face.boundingBox.centerY().toString())
                    Log.e("posisi-x^", face.boundingBox.exactCenterX().toString())
                    Log.e("posisi-y^", face.boundingBox.exactCenterY().toString())
                    val faceBitmap = Bitmap
                        .createBitmap(
                            bitmap,
                            (face.boundingBox.exactCenterX()/2).toInt(),
                            (face.boundingBox.exactCenterY()/2).toInt(),
                            face.boundingBox.width(),
                            face.boundingBox.height())
                    view_pict.setImageBitmap(faceBitmap)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val dataPath = data?.getStringExtra("data")
                if (dataPath != null) {
                    imagePath = dataPath
                }
                view_pict.setImageURI(Uri.parse(dataPath))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.permissionGranted(requestCode, 201, grantResults)) {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, REQ_CODE)
        }
    }
}
