package com.mnakasima.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.analytics.FirebaseAnalytics
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    var myEmail:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        var bundle:Bundle?=intent.extras
        myEmail = bundle!!.getString("email")

        incomingCalls()

    }

    fun buClick(view: View){

        var buSelected = view as Button

        var cellId = 0
        when(buSelected.id){
            R.id.bu1 -> cellId = 1
            R.id.bu2 -> cellId = 2
            R.id.bu3 -> cellId = 3
            R.id.bu4 -> cellId = 4
            R.id.bu5 -> cellId = 5
            R.id.bu6 -> cellId = 6
            R.id.bu7 -> cellId = 7
            R.id.bu8 -> cellId = 8
            R.id.bu9 -> cellId = 9
        }

       // playGame(cellId,buSelected)
        myRef.child("PlayerOnline").child(sessionID!!).child(cellId.toString()).setValue(myEmail)
    }

    var activePlayer = 1
    var player1 = arrayListOf<Int>()
    var player2 = arrayListOf<Int>()

    var countWin1 = 0
    var countWin2 = 0

    fun playGame(cellId:Int, buSelected:Button){

        if(activePlayer == 1){
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.blue)
            player1.add(cellId)
            activePlayer = 2
        }else{
            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.red)
            player2.add(cellId)
            activePlayer = 1
        }

        buSelected.isEnabled = false

        checkWinner()

    }

    fun checkWinner(){

        var winner = -1

        //1st row
        if (player1.contains(1) && player1.contains(2) && player1.contains(3)){
            winner = 1
        }
        if (player2.contains(1) && player2.contains(2) && player2.contains(3)){
            winner = 2
        }

        //2nd row
        if (player1.contains(4) && player1.contains(5) && player1.contains(6)){
            winner = 1
        }
        if (player2.contains(4) && player2.contains(5) && player2.contains(6)){
            winner = 2
        }

        //3rd row
        if (player1.contains(7) && player1.contains(8) && player1.contains(9)){
            winner = 1
        }
        if (player2.contains(7) && player2.contains(8) && player2.contains(9)){
            winner = 2
        }

        //1st column
        if (player1.contains(1) && player1.contains(4) && player1.contains(7)){
            winner = 1
        }
        if (player2.contains(1) && player2.contains(4) && player2.contains(7)){
            winner = 2
        }

        //2nd column
        if (player1.contains(2) && player1.contains(5) && player1.contains(8)){
            winner = 1
        }
        if (player2.contains(2) && player2.contains(5) && player2.contains(8)){
            winner = 2
        }

        //3rd column
        if (player1.contains(3) && player1.contains(6) && player1.contains(9)){
            winner = 1
        }
        if (player2.contains(3) && player2.contains(6) && player2.contains(9)){
            winner = 2
        }

        //1st diagonal
        if (player1.contains(1) && player1.contains(5) && player1.contains(9)){
            winner = 1
        }
        if (player2.contains(1) && player2.contains(5) && player2.contains(9)){
            winner = 2
        }

        //2nd diagonal
        if (player1.contains(3) && player1.contains(5) && player1.contains(7)){
            winner = 1
        }
        if (player2.contains(3) && player2.contains(5) && player2.contains(7)){
            winner = 2
        }

        if (winner == 1){
            countWin1++
            Toast.makeText(this,"Player 1 win the game",Toast.LENGTH_LONG).show()
            tv1.text = "Player 1 Wins\n $countWin1"
            resetGame()
        }else if (winner == 2){
            countWin2++
            Toast.makeText(this,"Player 2 win the game",Toast.LENGTH_LONG).show()
            tv2.text = "Player 2 Wins\n $countWin2"
            resetGame()
        }

        if((player1.size + player2.size) == 9 ){
            Toast.makeText(this,"DRAW",Toast.LENGTH_LONG).show()
            resetGame()
        }

    }

    fun autoPlay(cellID:Int){

        var buSelect:Button?
        when(cellID){
            1-> buSelect=bu1
            2-> buSelect=bu2
            3-> buSelect=bu3
            4-> buSelect=bu4
            5-> buSelect=bu5
            6-> buSelect=bu6
            7-> buSelect=bu7
            8-> buSelect=bu8
            9-> buSelect=bu9
            else->{
                buSelect=bu1
            }
        }

        playGame(cellID,buSelect)

    }

    fun resetGame(){

        activePlayer = 1

        player1.clear()
        player2.clear()

        for(cellId in 1..9){
            var buSelected:Button?

            buSelected = when(cellId){
                1 -> bu1
                2 -> bu2
                3 -> bu3
                4 -> bu4
                5 -> bu5
                6 -> bu6
                7 -> bu7
                8 -> bu8
                9 -> bu9
                else -> {bu1}
            }
            buSelected!!.text = ""
            buSelected!!.setBackgroundResource(R.color.colorButtonTicTacToe)
            buSelected.isEnabled = true

        }

    }

    fun buRequestEvent(view:View){
        var userEmail = etEmail.text.toString()
        myRef.child("Users").child(splitString(userEmail)).child("Request").push().setValue(myEmail)

        playerOnline(splitString(myEmail!!)+splitString(userEmail))
        playerSymbol="X"
    }

    fun buAcceptEvent(view:View){
        var userEmail = etEmail.text.toString()
        myRef.child("Users").child(splitString(userEmail)).child("Request").push().setValue(myEmail)

        playerOnline(splitString(userEmail)+splitString(myEmail!!))
        playerSymbol="O"
    }

    var sessionID:String?=null
    var playerSymbol:String?=null

    fun playerOnline(sessionID:String){
        this.sessionID=sessionID

        myRef.child("PlayerOnline").removeValue()
        myRef.child("PlayerOnline").child(sessionID)
            .addValueEventListener(object: ValueEventListener{

                override fun onCancelled(databaseError: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try{
                        player1.clear()
                        player2.clear()
                        var td = dataSnapshot!!.value as HashMap<String, Any>

                        if(td != null){
                            var value:String
                            for(key in td.keys){
                                value = td[key] as String

                                if(value!=myEmail){
                                    activePlayer = if(playerSymbol==="X") 1 else 2
                                }else{
                                    activePlayer = if(playerSymbol==="X") 2 else 1
                                }

                                autoPlay(key.toInt())

                            }

                        }

                    }catch(ex:Exception){

                    }
                }

            })
    }

    fun incomingCalls(){

        myRef.child("Users").child(splitString(myEmail!!)).child("Request")
            .addValueEventListener(object: ValueEventListener{

                override fun onCancelled(databaseError: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try{
                        var td = dataSnapshot!!.value as HashMap<String, Any>

                        if(td != null){
                            var value:String
                            for(key in td.keys){
                                value = td[key] as String
                                etEmail.setText(value)

                                myRef.child("Users").child(splitString(myEmail!!)).child("Request").setValue(true)

                                break
                            }

                        }

                    }catch(ex:Exception){

                    }
                }

            })

    }

    fun splitString(str:String):String{
        var split = str.split("@")
        return split[0]
    }

}
