package com.github.npcdw.store

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.npcdw.store.config.GlobalConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalConfig.getConfig(this@MainActivity)
        requestPermission()
        val goodsList = findViewById<Button>(R.id.goods_list)
        goodsList.setOnClickListener {
            val intent = Intent(this, GoodsListActivity::class.java)
            startActivity(intent)
        }
        val goodsStorage = findViewById<Button>(R.id.goods_storage)
        goodsStorage.setOnClickListener {
            val intent = Intent(this, GoodsAddActivity::class.java)
            startActivity(intent)
        }
        val goodsQuery = findViewById<Button>(R.id.goods_query)
        goodsQuery.setOnClickListener {
            val intent = Intent(this, ScanQRCodeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestPermission() {
        val permissionRead = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionRead != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }
        val permissionWrite = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE2
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            Log.i("", "request permission success")
        }
        if (requestCode == REQUEST_PERMISSION_CODE2) {
            Log.i("", "request permission success")
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1
        private const val REQUEST_PERMISSION_CODE2 = 2
    }
}