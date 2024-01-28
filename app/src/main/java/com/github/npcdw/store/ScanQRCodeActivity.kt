package com.github.npcdw.store

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.ZXingView

class ScanQRCodeActivity : AppCompatActivity(), QRCodeView.Delegate {
    private var mZXingView: ZXingView? = null
    private var jump = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qrcode)
        mZXingView = findViewById(R.id.zxingview)
        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        val permission = arrayOf(Manifest.permission.CAMERA)
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permission, 1024)
        } else {
            mZXingView!!.setDelegate(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1024) {
            val shouldFalse = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            if (shouldFalse) {
                AlertDialog.Builder(this)
                    .setTitle("权限申请")
                    .setMessage("请先设置授权访问相机权限")
                    .setPositiveButton("确定") { dialog: DialogInterface, _: Int -> dialog.cancel() }
                    .show()
            }
        }
    }

    public override fun onResume() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mZXingView!!.startSpotAndShowRect() // 显示扫描框，并开始识别
        }
        super.onResume()
    }

    public override fun onStart() {
        mZXingView!!.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
        super.onStart()
    }

    override fun onScanQRCodeSuccess(result: String) {
        mZXingView!!.startSpot()
        finish()
        if (jump) {
            return
        }
        jump = true
        val intent = Intent(this, GoodsDetailActivity::class.java)
        intent.putExtra("qrcode", result)
        startActivity(intent)
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
//        var tipText = mZXingView.scanBoxView.tipText;
//        val ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
//        if (isDark) {
//            if (!tipText.contains(ambientBrightnessTip)) {
//                mZXingView.scanBoxView.tipText = tipText + ambientBrightnessTip;
//            }
//        } else {
//            if (tipText.contains(ambientBrightnessTip)) {
//                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
//                mZXingView.scanBoxView.tipText = tipText;
//            }
//        }
    }

    override fun onScanQRCodeOpenCameraError() {}
    public override fun onStop() {
        mZXingView!!.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()
    }

    public override fun onDestroy() {
        mZXingView!!.onDestroy() // 销毁二维码扫描控件
        super.onDestroy()
    }
}