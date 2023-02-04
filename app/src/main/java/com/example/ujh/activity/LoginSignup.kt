package com.example.ujh.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.ujh.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class LoginSignup : AppCompatActivity() {


    lateinit var eMail: com.google.android.material.textfield.TextInputEditText
    lateinit var password: com.google.android.material.textfield.TextInputEditText
    lateinit var signIn: Button
    private lateinit var signUP: TextView
    lateinit var logIn: TextView
    lateinit var signUpLayout: LinearLayout
    private lateinit var logInLayout: LinearLayout

    lateinit var eMails: com.google.android.material.textfield.TextInputEditText
    lateinit var passwords01: com.google.android.material.textfield.TextInputEditText
    lateinit var passwords: com.google.android.material.textfield.TextInputEditText

    lateinit var auth: FirebaseAuth
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_signup)

        // View Binding
        signIn = findViewById(R.id.signIn)
        eMail = findViewById(R.id.eMail)
        password = findViewById(R.id.password)
        signUP = findViewById(R.id.signUP)
        logIn = findViewById(R.id.logIn)
        signUpLayout = findViewById(R.id.signUPLayout)
        logInLayout = findViewById(R.id.loginLayout)

        eMails = findViewById(R.id.eMails)
        passwords01 = findViewById(R.id.passwords01)
        passwords = findViewById(R.id.passwords)
        signIn = findViewById(R.id.signIn)


        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()

        signUP.setOnClickListener {
            signUP.background=resources.getDrawable(R.drawable.switch_trcks,null)
            signUP.setTextColor(resources.getColor(R.color.TextColor,null))
            logIn.background=null
            signUpLayout.visibility=View.VISIBLE
            logInLayout.visibility=View.GONE
            logIn.setTextColor(resources.getColor(R.color.blue,null))


            signIn.setOnClickListener {
                signUpUser()
                val intent = Intent(this, LoginSignup::class.java)
                startActivity(intent)
            }
        }
        logIn.setOnClickListener {
            signUP.background=null
            signUP.setTextColor(resources.getColor(R.color.blue,null))
            logIn.background=resources.getDrawable(R.drawable.switch_trcks,null)
            signUpLayout.visibility=View.GONE
            logInLayout.visibility=View.VISIBLE
            logIn.setTextColor(resources.getColor(R.color.TextColor,null))
        }



        signIn.setOnClickListener {
            login()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // using finish() to end the activity
            finish()
        }


        password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        password.transformationMethod = PasswordTransformationMethod.getInstance()





    }

    private fun signUpUser() {
        val email = eMails.text.toString()
        val pass = passwords.text.toString()
        val confirmPassword = passwords01.text.toString()

        // check pass
        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // If all credential are correct
        // We call createUserWithEmailAndPassword
        // using auth object and pass the
        // email and pass in it.
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun login() {
        val email = eMail.text.toString()
        val pass = password.text.toString()
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }
}