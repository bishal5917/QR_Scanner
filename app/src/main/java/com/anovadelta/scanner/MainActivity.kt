package com.anovadelta.scanner

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

private const val CAMERA_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        codeScanner()
    }

    private fun codeScanner(){
        val scanView = findViewById<CodeScannerView>(R.id.scanView)
        val tvDetail = findViewById<TextView>(R.id.tvScanDetail)
        codeScanner = CodeScanner(this,scanView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats=CodeScanner.ALL_FORMATS
            autoFocusMode=AutoFocusMode.SAFE
            scanMode=ScanMode.CONTINUOUS
            isAutoFocusEnabled=true
            isFlashEnabled=false
            decodeCallback= DecodeCallback {
                runOnUiThread{
                    tvDetail.text = it.text
                }
            }
            errorCallback= ErrorCallback {
                runOnUiThread{
                    tvDetail.text = "Error : ${it.message}"
                }
            }
        }
    }

    override fun onResume() {
        codeScanner.startPreview()
        super.onResume()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
       ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
           CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            CAMERA_REQUEST_CODE->{
               if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                  Toast.makeText(this,"Camera permission required",Toast.LENGTH_SHORT).show()
               }else{
                   //success
               }
            }
        }
    }
}