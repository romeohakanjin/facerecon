package com.facerocon.rekon.ui.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facerocon.rekon.R
import com.facerocon.rekon.ui.home.HomeViewModel
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.activity_main.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var cameraSurfaceHolder: SurfaceHolder
    private lateinit var cameraSource: CameraSource
    private lateinit var textRecognizer: TextRecognizer

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView? = root?.findViewById(R.id.photoTextView)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView?.text = it
        })

        // initialize text recognizer
        textRecognizer = TextRecognizer.Builder(this.context).build()

        // initialize camera source with phone parameters
        cameraSource = CameraSource.Builder(this.context, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedFps(3.0f)
                .build()

        // callback for the end of the SurfaceHolder creation from main activity
        cameraSurfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                // recuperation of the holder of the component
                cameraSurfaceHolder = cameraSurfaceView.holder

                // permission verification
                if (ActivityCompat.checkSelfPermission(context!!,
                                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@HomeFragment.context, "Permission denied for the camera", Toast.LENGTH_SHORT).show()
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

        return root
    }
}