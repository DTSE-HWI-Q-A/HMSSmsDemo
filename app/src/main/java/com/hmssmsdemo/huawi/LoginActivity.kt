package com.hmssmsdemo.huawi

import android.content.Intent
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.huawei.agconnect.auth.*
import com.huawei.agconnect.auth.VerifyCodeSettings.ACTION_REGISTER_LOGIN
import com.huawei.agconnect.auth.VerifyCodeSettings.OnVerifyCodeCallBack
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class LoginActivity : AppCompatActivity() {

    var settings: VerifyCodeSettings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar.visibility = View.GONE

        settings = VerifyCodeSettings.newBuilder()
            .action(ACTION_REGISTER_LOGIN) //ACTION_REGISTER_LOGIN/
            .sendInterval(30) // Minimum sending interval, ranging from 30s to 120s.
            .locale(Locale.getDefault()) // Language in which a verification code is sent, which is optional. The default value is Locale.getDefault.
            .build()

        loginBtn.setOnClickListener {
            if (phoneNumberEt.text.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE

                PhoneAuthProvider.verifyPhoneCode(
                    "57", // Country Code
                    phoneNumberEt.text.toString(), // Phone number
                    settings,
                    object : OnVerifyCodeCallBack {
                        override fun onVerifySuccess(
                            shortestInterval: String,
                            validityPeriod: String
                        ) {
                            progressBar.visibility = View.GONE
                            val builder = AlertDialog.Builder(this@LoginActivity)
                            val dialog: AlertDialog = builder.create()
                            val inflater = layoutInflater
                            val dialogLayout =
                                inflater.inflate(R.layout.dialog_verification_code, null)
                            val verifyBtn = dialogLayout.findViewById<Button>(R.id.verifyBtn)
                            val smsCodeEt = dialogLayout.findViewById<EditText>(R.id.smsCodeEt)
                            verifyBtn.setOnClickListener {
                                if (smsCodeEt.text.isNotEmpty()) {
                                    progressBar.visibility = View.VISIBLE
                                    val phoneUser: PhoneUser = PhoneUser.Builder()
                                        .setCountryCode("57") // Country Code
                                        .setPhoneNumber(phoneNumberEt.text.toString()) // The value of phoneNumber must contain the country code and mobile number.
                                        .setVerifyCode(smsCodeEt.text.toString())
                                        .build()
                                    AGConnectAuth.getInstance().createUser(phoneUser)
                                        .addOnCompleteListener(this@LoginActivity) { task ->
                                            if (task.isSuccessful) {
                                                progressBar.visibility = View.GONE
                                                val intent =
                                                    Intent(
                                                        this@LoginActivity,
                                                        HomeActivity::class.java
                                                    )
                                                    dialog.dismiss()
                                                startActivity(intent)
                                            } else {
                                                val error = task.exception.toString()
                                                val errorCode = "203818130" // Codigo de la expcecion cuando el usuario ya esta registrado
                                                if (error.contains(errorCode)) {
                                                    progressBar.visibility = View.GONE
                                                    val intent =
                                                        Intent(
                                                            this@LoginActivity,
                                                            HomeActivity::class.java
                                                        )
                                                    dialog.dismiss()
                                                    startActivity(intent)
                                                } else {
                                                    progressBar.visibility = View.GONE
                                                    Toast.makeText(
                                                        this@LoginActivity,
                                                        "Something went wrong!",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    Log.e("Error: ", error)
                                                    dialog.dismiss()
                                                }
                                            }
                                        }
                                } else {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "SMS Code must not be empty!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            dialog.setView(dialogLayout)
                            dialog.setCancelable(false)
                            dialog.show()
                        }

                        override fun onVerifyFailure(e: java.lang.Exception) {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@LoginActivity,
                                "Something went wrong!",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("Error: ", e.toString())
                        }
                    })
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Phone number must not be empty!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
