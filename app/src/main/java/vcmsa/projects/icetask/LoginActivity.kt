package vcmsa.projects.icetask

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegisterPage: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this) // ✅ Ensure Firebase is initialized

        try {
            auth = FirebaseAuth.getInstance() // ✅ Safe Firebase Auth initialization
        } catch (e: Exception) {
            Log.e("LoginActivity", "FirebaseAuth initialization failed", e)
            Toast.makeText(this, "Firebase initialization error", Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegisterPage = findViewById(R.id.btnRegisterPage)

        // Set up listeners for buttons
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegisterPage.setOnClickListener {
            // This should navigate to the RegisterActivity
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        if (!::auth.isInitialized) {
            Toast.makeText(this, "Firebase Auth not initialized", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    if (user != null) {
                        sendWelcomeEmail(user.email!!)
                        navigateToMain()
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendWelcomeEmail(userEmail: String) {
        Toast.makeText(this, "Welcome back, $userEmail!", Toast.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
