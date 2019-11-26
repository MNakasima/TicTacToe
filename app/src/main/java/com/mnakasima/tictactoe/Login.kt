package com.mnakasima.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    override fun onStart() {
        super.onStart()
        loadMain()
    }

    fun buLoginEvent(view: View){

        loginToFirebase(etEmail.text.toString(), etPassword.text.toString())

    }

    fun loginToFirebase(email:String,password:String){

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                task ->

                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "Success Login", Toast.LENGTH_LONG).show()

                    var currentUser=mAuth!!.currentUser

                    //save in
                    if(currentUser!=null) {
                        myRef.child("Users").child(splitString(currentUser.email.toString())).child("Request").setValue(currentUser.uid)
                    }
                    loadMain()
                }else{
                    Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_LONG).show()
                }

            }

    }

    fun loadMain(){

        var currentUser=mAuth!!.currentUser

        if(currentUser!=null){

            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser!!.email)
            intent.putExtra("uid", currentUser!!.uid)

            startActivity(intent)
        }

    }

    fun splitString(str:String):String{
        var split = str.split("@")
        return split[0]
    }

}
