package tw.edu.pu.csim.tcyang.firebaseemailauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    lateinit var email:String
    lateinit var password:String
    lateinit var flag:String
    var UID:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 獲取FirebaseAuth對象的共享實例
        auth = Firebase.auth
    }
}