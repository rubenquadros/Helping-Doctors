package consultdocs.com.sub.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ProgressBar
import consultdocs.com.sub.R
import consultdocs.com.sub.bean.UserData
import consultdocs.com.sub.services.FirebaseServices
import consultdocs.com.sub.utility.ApplicationConstants
import consultdocs.com.sub.utility.ApplicationUtility
import consultdocs.com.sub.utility.GotUserData
import consultdocs.com.sub.utility.TaskOnComplete
import kotlinx.android.synthetic.main.activity_home_screen.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.regex.Pattern

class Profile : AppCompatActivity(), GotUserData, TaskOnComplete {

    private lateinit var mProgressBar: ProgressBar
    private val firebaseServices = FirebaseServices()
    private lateinit var oldMsisdn: String
    private lateinit var newMsisdn: String
    private lateinit var oldEmail: String
    private lateinit var newEmail: String
    private lateinit var name: String
    private lateinit var gender: String
    private lateinit var dob: String
    private var emptyEmail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupToolBar()
        setupUI()
        retrieveUserData()
        save_button.setOnClickListener {
            validateAndUpdate()
        }
    }

    private fun validateAndUpdate() {
        newEmail = if(!emptyEmail){
            email_edit_text.text.toString().trim()
        }else{
            oldEmail
        }
        newMsisdn = msisdn_edit_text.text.toString().trim()
        if(newEmail == oldEmail && newMsisdn == oldMsisdn){
            ApplicationUtility.showSnack(this.resources.getString(R.string.no_change_prof), root_layout_profile, ApplicationConstants.ACTION_OK)
            return
        }
        if(ApplicationUtility.regexValidator(Pattern.compile(ApplicationConstants.EMAIL_REGEX, Pattern.CASE_INSENSITIVE), newEmail) == ApplicationConstants.FAIL_RESP){
            ApplicationUtility.showSnack(this.resources.getString(R.string.email_err), root_layout_profile, ApplicationConstants.ACTION_OK)
            return
        }
        if(ApplicationUtility.regexValidator(Pattern.compile(ApplicationConstants.MSISDN_REGEX),newMsisdn) == ApplicationConstants.FAIL_RESP){
            ApplicationUtility.showSnack(this.resources.getString(R.string.msisdn_err), root_layout_profile, ApplicationConstants.ACTION_OK)
            return
        }
        if(newMsisdn == oldMsisdn) {
            updateEmail(newEmail)
        }else{
            ApplicationUtility.showDialog(this@Profile, this.resources.getString(R.string.prof_edited), this.resources.getString(R.string.num_edited), this as TaskOnComplete, ApplicationConstants.NUM_EDITED)
        }
    }

    private fun retrieveUserData() {
        mProgressBar.visibility = View.VISIBLE
        firebaseServices.setUserListener(this as GotUserData)
        val msisdn = ApplicationUtility.readValue(this, ApplicationConstants.MSISDN)
        if(msisdn == ApplicationConstants.EMPTY){
            mProgressBar.visibility = View.GONE
            ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), drawer_layout, ApplicationConstants.ACTION_OK)
            return
        }
        firebaseServices.getUserDetails(msisdn)
    }

    override fun receivedData(response: UserData?) {
        mProgressBar.visibility = View.GONE
        if(response == null){
            ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), drawer_layout, ApplicationConstants.ACTION_OK)
            return
        }
        name = response.name!!
        gender = response.gender!!
        dob = response.dob!!
        name_edit_text.setText(name)
        if(response.email == ApplicationConstants.EMPTY){
            email_text.visibility = View.GONE
            email_edit_text.visibility = View.GONE
            emptyEmail = true
        }
        oldEmail = response.email!!
        email_edit_text.setText(oldEmail)
        oldMsisdn = response.msisdn!!
        msisdn_edit_text.setText(oldMsisdn)
        gender_edit_text.setText(gender)
        dob_edit_text.setText(dob)
    }

    private fun updateEmail(email: String) {
        mProgressBar.visibility = View.VISIBLE
        firebaseServices.setListener(this as TaskOnComplete)
        firebaseServices.upDateUserEmail(newMsisdn, email)
    }

    private fun updateProfile(msisdn: String) {
        val userData = UserData(email = newEmail, msisdn = msisdn, gender = gender, dob = dob)
        firebaseServices.setListener(this as TaskOnComplete)
        firebaseServices.saveUser(newMsisdn, userData, ApplicationConstants.EMPTY, oldMsisdn)
    }

    private fun setupUI() {
        mProgressBar = findViewById(R.id.profileProgressBar)

        name_text.typeface = ApplicationUtility.fontRegular(this)
        name_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        name_edit_text.typeface = ApplicationUtility.fontRegular(this)
        name_edit_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        email_text.typeface = ApplicationUtility.fontRegular(this)
        email_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        email_edit_text.typeface = ApplicationUtility.fontRegular(this)
        email_edit_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        msisdn_text.typeface = ApplicationUtility.fontRegular(this)
        msisdn_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        msisdn_edit_text.typeface = ApplicationUtility.fontRegular(this)
        msisdn_edit_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        gender_text.typeface = ApplicationUtility.fontRegular(this)
        gender_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        gender_edit_text.typeface = ApplicationUtility.fontRegular(this)
        gender_edit_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        dob_text.typeface = ApplicationUtility.fontRegular(this)
        dob_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        dob_edit_text.typeface = ApplicationUtility.fontRegular(this)
        dob_edit_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        save_button.typeface = ApplicationUtility.fontBold(this)
        save_button.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_LARGE)
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        toolbarTitle.text = getString(R.string.profile)
        toolbarTitle.typeface = ApplicationUtility.fontBold(this)
        toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        if(oldEmail == email_edit_text.text.toString().trim() && oldMsisdn == msisdn_edit_text.text.toString().trim()) {
            onBackPressed()
            return true
        }
        ApplicationUtility.showDialog(this@Profile, this.resources.getString(R.string.prof_edited), this.resources.getString(R.string.prof_edited_msg), this as TaskOnComplete, ApplicationConstants.EMPTY)
        return false
    }

    override fun onResponseReceived(response: String) {
        when(response) {
            ApplicationConstants.FAIL_RESP -> {
                onBackPressed()
            }
            ApplicationConstants.SUCCESS_RESP -> {
                validateAndUpdate()
            }
            ApplicationConstants.UPDATE_EMAIL_FAIL -> {
                ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), root_layout_profile, ApplicationConstants.ACTION_OK, this as TaskOnComplete, ApplicationConstants.EMPTY)
                return
            }
            ApplicationConstants.UPDATE_EMAIL_SUCCESS -> {
                ApplicationUtility.showSnack(this.resources.getString(R.string.email_update_succ), root_layout_profile, ApplicationConstants.ACTION_OK, this as TaskOnComplete, ApplicationConstants.EMPTY)
                return
            }
            ApplicationConstants.NUM_EDITED_GO -> {
                updateProfile(newMsisdn)
            }
            ApplicationConstants.NUM_EDITED_NOGO -> {
                return
            }
            ApplicationConstants.GOTO_LOGIN -> {
                ApplicationUtility.doLogout(this@Profile)
            }
            ApplicationConstants.UPDATE_PROF_SUCCESS -> {
                ApplicationUtility.showSnack(this.resources.getString(R.string.prof_update_succ), root_layout_profile, ApplicationConstants.ACTION_OK, this as TaskOnComplete, ApplicationConstants.UPDATE_PROF_SUCCESS)
            }
            ApplicationConstants.SNACKBAR_ACTION -> {
                retrieveUserData()
                return
            }
            ApplicationConstants.TASK_CANCELLED -> {
                mProgressBar.visibility = View.GONE
                ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), root_layout_profile, ApplicationConstants.ACTION_OK)
                return
            }
            else -> {
                ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), root_layout_profile, ApplicationConstants.ACTION_OK)
                return
            }
        }
    }
}
