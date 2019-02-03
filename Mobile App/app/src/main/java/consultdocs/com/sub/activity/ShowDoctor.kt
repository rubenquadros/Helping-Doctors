package consultdocs.com.sub.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import consultdocs.com.sub.R
import consultdocs.com.sub.utility.ApplicationConstants
import consultdocs.com.sub.utility.ApplicationUtility
import kotlinx.android.synthetic.main.activity_show_doctor.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ShowDoctor : AppCompatActivity() {

    private lateinit var docName: String
    private lateinit var fromTime: String
    private lateinit var toTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_doctor)
        readIntents()
        setupToolBar()
        setupUI()
        audio_call_doc.setOnClickListener {
            startActivity(Intent(this, AudioCall::class.java))
        }
        video_call_doc.setOnClickListener {
            startActivity(Intent(this, VideoCall::class.java))
        }
        text_doc.setOnClickListener {
            //chat with doc
        }
    }

    private fun readIntents() {
        docName = intent.getStringExtra(ApplicationConstants.DOCTOR_NAME)
        fromTime = intent.getStringExtra(ApplicationConstants.DOC_FROM_TIME)
        toTime = intent.getStringExtra(ApplicationConstants.DOC_TO_TIME)
    }

    private fun setupUI() {
        doctor_name.typeface = ApplicationUtility.fontRegular(this)
        doctor_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        doctor_name.text = docName

        doctor_timing.typeface = ApplicationUtility.fontRegular(this)
        doctor_timing.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        doctor_timing.text = this.resources.getString(R.string.after_available)
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        toolbarTitle.text = docName
        toolbarTitle.typeface = ApplicationUtility.fontBold(this)
        toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        onBackPressed()
        return true
    }
}
