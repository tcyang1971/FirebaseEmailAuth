package tw.edu.pu.csim.tcyang.firebaseemailauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

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

        //註冊
        btnReg.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                email = edtEmail.text.toString()
                password = edtPassword.text.toString()
                flag="註冊"

                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                updateUI(user)
                            } else {
                                Toast.makeText(baseContext, "註冊失敗：" + task.exception?.message,
                                        Toast.LENGTH_SHORT).show()
                                updateUI(null)
                            }
                        }
            }
        })

        //登入
        btnLogIn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                email = edtEmail.text.toString()
                password = edtPassword.text.toString()
                flag="登入"

                auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                updateUI(user)
                            } else {
                                Toast.makeText(baseContext, "登入失敗：" + task.exception?.message,
                                        Toast.LENGTH_SHORT).show()
                                updateUI(null)
                            }
                        }
            }
        })

        //登出
        btnLogOut.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                Firebase.auth.signOut()
                Toast.makeText(baseContext, "您已成功登出",
                        Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(fUser: FirebaseUser?) {
        if (fUser != null) {
            UID = fUser.uid.toString()
            when (flag){
                "註冊" -> {
                    Toast.makeText(baseContext, "恭喜您註冊成功\n您的UID為：" + UID,
                            Toast.LENGTH_LONG).show()
                }
                "登入" -> {
                    Toast.makeText(baseContext, "恭喜您登入成功\n您的UID為：" + UID,
                            Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //初始化活動時，先檢查用戶當前是否登錄
    public override fun onStart() {
        super.onStart()
        var user = auth.currentUser
        if(user != null){
            flag="登入"
            updateUI(user)
        }
    }

}