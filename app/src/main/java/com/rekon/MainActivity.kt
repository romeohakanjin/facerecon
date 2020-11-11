package com.rekon

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

/**
 *
 * @author Roméo HAKANJIN
 */
class MainActivity : AppCompatActivity() {
    private lateinit var cameraSurfaceHolder: SurfaceHolder
    private lateinit var cameraSource: CameraSource
    private lateinit var textRecognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize text recognizer
        textRecognizer = TextRecognizer.Builder(this).build()

        // initialize camera source with phone parameters
        cameraSource = CameraSource.Builder(this, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_FRONT)
            .setAutoFocusEnabled(true)
            .setRequestedFps(3.0f)
            .build()

        // callback for the end of the SurfaceHolder creation from main activity
        cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                // recuperation of the holder of the component
                cameraSurfaceHolder = cameraSurfaceView.holder

                // permission verification
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(applicationContext, "permission denied for the camera", Toast.LENGTH_SHORT).show()
                } else {
                    // start camera
                    cameraSource.start(cameraSurfaceHolder)
                }
            }
        })
    }
}
