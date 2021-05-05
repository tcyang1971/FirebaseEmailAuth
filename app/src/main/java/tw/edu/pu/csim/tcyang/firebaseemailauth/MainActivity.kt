package tw.edu.pu.csim.tcyang.firebaseemailauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    lateinit var email:String
    lateinit var password:String
    lateinit var flag:String
    var UID:String = ""

    var db = FirebaseFirestore.getInstance()
    var userData: MutableMap<String, Any> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnReg.isEnabled = true
        btnLogIn.isEnabled = true
        btnLogOut.isEnabled = false
        btnChange.isEnabled = false
        btnHero.isEnabled = false

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

                                //寄送驗證信
                                user!!.sendEmailVerification()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(baseContext, "請至您的信箱收信完成驗證程序",
                                                        Toast.LENGTH_SHORT).show()
                                            }
                                        }
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
                btnReg.isEnabled = true
                btnLogIn.isEnabled = true
                btnLogOut.isEnabled = false
                btnChange.isEnabled = false
                btnHero.isEnabled = false
            }
        })

        //修改成績
        btnChange.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                var user = auth.currentUser
                if(user != null){
                    flag="修改"
                    updateUI(user)
                }
                else{
                    Toast.makeText(baseContext, "請先登入再修改成績", Toast.LENGTH_LONG).show()
                }
            }
        })

        btnHero.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                var user = auth.currentUser
                if(user != null){
                    flag="排名"
                    updateUI(user)
                }
                else{
                    Toast.makeText(baseContext, "請先登入再查詢成績", Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun updateUI(fUser: FirebaseUser?) {
        if (fUser != null) {
            //btnReg.isEnabled = false
            //btnLogIn.isEnabled = false
            //btnLogOut.isEnabled = true
            //btnChange.isEnabled = true
            //btnHero.isEnabled = true

            UID = fUser.uid.toString()
            when (flag){
                "註冊" -> {
                    userData["名字"] = edtEmail.text.toString()
                    userData["分數"] = 0
                    db.collection("Users")
                            .document(UID)
                            .set(userData)
                            .addOnSuccessListener {
                                //Toast.makeText(baseContext, "恭喜您註冊成功\n您的UID為：" + UID,
                                //        Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(baseContext, "註冊失敗：" + e.toString(),
                                        Toast.LENGTH_LONG).show()
                            }
                }
                "登入" -> {
                    //讀取資料
                    db.collection("Users")
                            .document(UID)
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val msg = "恭喜您登入成功\n您的成績為：" + task.result!!.data?.get("分數").toString()
                                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                }

                "修改" -> {
                    userData["分數"] = edtScore.text.toString().toInt()
                    db.collection("Users")
                            //.add(user)
                            .document(UID)
                            .update(userData)
                            .addOnSuccessListener {
                                Toast.makeText(baseContext, "資料修改成功", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(baseContext, "資料修改失敗：" + e.toString(), Toast.LENGTH_LONG).show()
                            }
                }

                "排名" -> {
                    db.collection("Users")
                            .orderBy("分數", Query.Direction.DESCENDING)
                            .limit(3)
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    var msg:String = ""
                                    for (document in task.result!!) {
                                        msg += "名字：" + document.data["名字"] +
                                                "\n成績：" + document.data["分數"].toString() + "\n\n"
                                    }
                                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                                }

                                else{
                                    Toast.makeText(baseContext, "查詢失敗：" + task.exception.toString(),
                                            Toast.LENGTH_LONG).show()
                                }
                            }
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