package com.rakaadinugroho.imagefacecroper

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.wonderkiln.camerakit.*

import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileOutputStream

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        camera_view.addCameraKitListener(object : CameraKitEventListener {
            override fun onVideo(p0: CameraKitVideo?) {}

            override fun onEvent(p0: CameraKitEvent?) {}

            override fun onImage(cameraKitImage: CameraKitImage?) {
                if (cameraKitImage != null) {
                    val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Alu")
                    if (!dir.exists()) dir.mkdirs()
                    val pictFile = File(dir, "Deta.jpg")
                    if (pictFile.exists()) pictFile.delete()
                    val fou = FileOutputStream(pictFile)
                    cameraKitImage.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fou)
                    fou.flush()
                    fou.close()
                    backToPreview(pictFile.absolutePath)
                } else {

                }
            }

            override fun onError(cameraKitError: CameraKitError?) {
                Log.e("error", "Something Error ${cameraKitError?.message}")
            }

        })
        camera_view.facing = CameraKit.Constants.FACING_FRONT

        take_camera.setOnClickListener {
            camera_view.captureImage()
        }
    }

    private fun backToPreview(absolutePath: String?){
        val intent = Intent()
        intent.putExtra("data", absolutePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        camera_view.start()
    }

    override fun onPause() {
        super.onPause()
        camera_view.stop()
    }

}
