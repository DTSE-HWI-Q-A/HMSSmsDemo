package com.hmssmsdemo.huawi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.huawei.agconnect.auth.AGConnectAuth

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AGConnectAuth.getInstance().signOut()
    }
}
