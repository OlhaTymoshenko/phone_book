package ua.com.amicablesoft.phonebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.transition.Scene
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import ua.com.amicablesoft.phonebook.dal.Repository

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        if (preferences.contains("user")) {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContentView(R.layout.activity_splash)
            val sceneRootView = findViewById(R.id.splash_scene_root) as ViewGroup
            val sceneB = Scene.getSceneForLayout(sceneRootView, R.layout.activity_splash_scene_b, this)
            Handler(Looper.getMainLooper()).postDelayed({
                TransitionManager.go(sceneB)
                val signInButton = findViewById(R.id.sign_in_button)
                signInButton.setOnClickListener {
                    attemptSignIn()
                }
            }, 1500)
        }

    }

    private fun attemptSignIn() {
        val loginInputLayout = findViewById(R.id.login_input_layout) as TextInputLayout
        loginInputLayout.error = null
        val passwordInputLayout = findViewById(R.id.password_input_layout) as TextInputLayout
        passwordInputLayout.error = null
        val loginView = findViewById(R.id.login_edit_text) as TextInputEditText
        val login = loginView.text.toString()
        val passwordView = findViewById(R.id.password_edit_text) as TextInputEditText
        val password = passwordView.text.toString()
        var cancel = false
        var focusView: View? = null
        val user = Repository(applicationContext).findUser()
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.error = getText(R.string.error_required_field)
            focusView = passwordInputLayout
            cancel = true
        } else if (password != user.password) {
            passwordInputLayout.error = getText(R.string.error_invalid_password)
            focusView = passwordInputLayout
            cancel = true
        }
        if (TextUtils.isEmpty(login)) {
            loginInputLayout.error = (getText(R.string.error_required_field))
            focusView = loginInputLayout
            cancel = true
        } else if (login != user.login) {
            loginInputLayout.error = getText(R.string.error_invalid_login)
            focusView = loginInputLayout
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            val preferences = getSharedPreferences("user", Context.MODE_PRIVATE)
            preferences.edit().putString("user", "signed_in").apply()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
