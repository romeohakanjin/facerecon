package com.facerocon.rekon

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Face, text, object recognition using api 'Vision Mobile' from google api
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
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedFps(3.0f)
                .build()

        // callback for the end of the SurfaceHolder creation from main activity
        cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                // recuperation of the holder of the component
                cameraSurfaceHolder = cameraSurfaceView.holder

                // permission verification
                if (ActivityCompat.checkSelfPermission(this@MainActivity,
                                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(applicationContext, "permission denied for the camera", Toast.LENGTH_SHORT).show()
                } else {
                    // start camera
                    cameraSource.start(cameraSurfaceHolder)
                }
            }
        })

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<TextBlock>?) {
                // recuperation of text detected on the camera
                if (detections!= null) {
                    val itemsDetected: SparseArray<TextBlock> = detections.detectedItems

                    // initialize the textView from main activity with the the content of all the text elements
                    photoTextView.text = (0 until itemsDetected.size()).joinToString("\n") { item ->
                        itemsDetected.get(item).value
                    }
                }
            }
        })
    }
}