package consultdocs.com.sub.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import consultdocs.com.sub.R
import consultdocs.com.sub.utility.ApplicationConstants

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            startActivity(Intent(this, Login::class.java))
            finish()
        }, ApplicationConstants.SPLASH_TIME)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
