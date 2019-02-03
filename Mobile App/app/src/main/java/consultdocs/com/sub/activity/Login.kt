package consultdocs.com.sub.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.TextInputEditText
import android.support.v4.app.ActivityCompat
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.PhoneAuthProvider
import consultdocs.com.sub.R
import consultdocs.com.sub.services.AgoraServices
import consultdocs.com.sub.services.FirebaseServices
import consultdocs.com.sub.utility.*
import io.agora.IAgoraAPI
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class Login : AppCompatActivity(), TaskOnComplete, GotNumbers {

    private lateinit var resendText: TextView
    private lateinit var loginButton: Button
    private lateinit var cancelButton: Button
    private var isOTPAuthenticationInProgress = true
    private lateinit var otp: String
    private lateinit var mobile: String
    private val firebaseServices = FirebaseServices()
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mobileNumber: String
    private lateinit var appID: String
    private var isExists = false
    private var myCountDownTimer = MyCountDownTimer((ApplicationConstants.OTP_TIME * 1000), 1000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupToolbar()
        setupUI()
        appID = this.resources.getString(R.string.agora_app_id)
        mProgressBar = findViewById(R.id.loginProgressBar)
        signUp.setOnClickListener {
            startActivity(Intent(this, Registration::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        addCallback()
    }

    private fun addCallback() {
        AgoraServices.the().getmAgoraAPI()!!.callbackSet(object : IAgoraAPI.ICallBack {
            override fun onChannelQueryUserIsIn(p0: String?, p1: String?, p2: Int) {

            }

            override fun onMsg(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInviteFailed(p0: String?, p1: String?, p2: Int, p3: Int, p4: String?) {

            }

            override fun onInviteMsg(p0: String?, p1: String?, p2: Int, p3: String?, p4: String?, p5: String?) {

            }

            override fun onChannelUserLeaved(p0: String?, p1: Int) {

            }

            override fun onChannelAttrUpdated(p0: String?, p1: String?, p2: String?, p3: String?) {

            }

            override fun onUserAttrResult(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInviteAcceptedByPeer(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onMessageSendError(p0: String?, p1: Int) {

            }

            override fun onUserAttrAllResult(p0: String?, p1: String?) {

            }

            override fun onReconnected(p0: Int) {

            }

            override fun onMessageAppReceived(p0: String?) {

            }

            override fun onBCCall_result(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInvokeRet(p0: String?, p1: String?, p2: String?) {

            }

            override fun onChannelUserList(p0: Array<out String>?, p1: IntArray?) {

            }

            override fun onReconnecting(p0: Int) {

            }

            override fun onChannelJoinFailed(p0: String?, p1: Int) {

            }

            override fun onQueryUserStatusResult(p0: String?, p1: String?) {

            }

            override fun onMessageChannelReceive(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onInviteReceived(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onLog(p0: String?) {

            }

            override fun onInviteReceivedByPeer(p0: String?, p1: String?, p2: Int) {

            }

            override fun onDbg(p0: String?, p1: ByteArray?) {

            }

            override fun onInviteRefusedByPeer(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onInviteEndByPeer(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onChannelJoined(p0: String?) {

            }

            override fun onInviteEndByMyself(p0: String?, p1: String?, p2: Int) {

            }

            override fun onMessageSendSuccess(p0: String?) {

            }

            override fun onMessageInstantReceive(p0: String?, p1: Int, p2: String?) {

            }

            override fun onChannelUserJoined(p0: String?, p1: Int) {

            }

            override fun onChannelQueryUserNumResult(p0: String?, p1: Int, p2: Int) {

            }

            override fun onChannelLeaved(p0: String?, p1: Int) {

            }

            override fun onMessageSendProgress(p0: String?, p1: String?, p2: String?, p3: String?) {

            }

            override fun onError(p0: String?, p1: Int, p2: String?) {

            }

            override fun onLoginSuccess(p0: Int, p1: Int) {

            }

            override fun onLoginFailed(p0: Int) {
                runOnUiThread {
                    if (p0 == IAgoraAPI.ECODE_LOGIN_E_NET) {
                        ApplicationUtility.showToast(this@Login, this@Login.resources.getString(R.string.agora_login_fail))
                    }
                }
            }

            override fun onLogout(p0: Int) {

            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun login(view: View){
        mobile = mobileNum.text.toString().trim()
        if(msisdnValidation(mobile) == ApplicationConstants.MSISDN_NOMATCH){
            ApplicationUtility.showSnack(this.resources.getString(R.string.msisdn_err), root_layout_login, ApplicationConstants.ACTION_OK)
            return
        }
        askPermissions()
    }

    private fun checkNumExists() {
        mProgressBar.visibility = View.VISIBLE
        mobileNumber = ApplicationConstants.COUNTRY_CODE + mobile
        checkNum()
    }

    private fun checkNum() {
        firebaseServices.setNumListner(this as GotNumbers)
        firebaseServices.checkNumber()
    }

    private fun doLogin() {
        agoraLogin()
        authenticateUser()
    }

    private fun agoraLogin() {
        AgoraServices.the().getmAgoraAPI()!!.login2(appID, mobile, "_no_need_token", 0, "", 5, 1)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPermissions() {
        if(checkSelfPermission(ApplicationConstants.permissions[0]) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(ApplicationConstants.permissions[1]) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(ApplicationConstants.permissions[2]) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(ApplicationConstants.permissions[3]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@Login,ApplicationConstants.permissions, ApplicationConstants.REQUEST_CODE)
        }else{
            checkNumExists()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            ApplicationConstants.REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED){
                    ApplicationUtility.showSnack(this.resources.getString(R.string.permission_req), root_layout_login, ApplicationConstants.ACTION_OK, this as TaskOnComplete, ApplicationConstants.EMPTY)
                }else{
                    checkNumExists()
                }
            }
        }
    }

    fun msisdnValidation(mobile: String): String {
        if(ApplicationUtility.regexValidator(Pattern.compile(ApplicationConstants.MSISDN_REGEX), mobile) == ApplicationConstants.FAIL_RESP){
            return ApplicationConstants.MSISDN_NOMATCH
        }
        return ApplicationConstants.SUCCESS_RESP
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResponseReceived(response: String) {
        when(response) {
            ApplicationConstants.CREATE_AUTH_FAIL -> {
                mProgressBar.visibility = View.GONE
                ApplicationUtility.showSnack(this.resources.getString(R.string.login_err), root_layout_login, ApplicationConstants.ACTION_OK)
                return
            }
            ApplicationConstants.AUTH_SUCCESS -> {
                //otp success
            }
            ApplicationConstants.CODE_SENT -> {
                mProgressBar.visibility = View.GONE
            }
            ApplicationConstants.CREATE_AUTH_SUCCESS -> {
                mProgressBar.visibility = View.GONE
                ApplicationUtility.storeValue(ApplicationConstants.MSISDN, mobile, this)
                startActivity(Intent(this, HomeScreen::class.java))
            }
            ApplicationConstants.SNACKBAR_ACTION -> {
                askPermissions()
            }
            ApplicationConstants.CODE_TIME_OUT -> {
                mProgressBar.visibility = View.GONE
                ApplicationUtility.showSnack(this.resources.getString(R.string.no_otp), root_layout_login, ApplicationConstants.ACTION_OK)
                return
            }
            ApplicationConstants.TASK_CANCELLED -> {
                mProgressBar.visibility = View.GONE
                ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), root_layout_login, ApplicationConstants.ACTION_OK)
                return
            }
            ApplicationConstants.SUCCESS_RESP -> {
                startActivity(Intent(this, Registration::class.java))
            }
            ApplicationConstants.FAIL_RESP -> {
                //do nothing
            }
            else -> {
                mProgressBar.visibility = View.GONE
                ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), root_layout_login, ApplicationConstants.ACTION_OK)
                return
            }
        }
    }

    override fun onNumbersReceived(numList: ArrayList<String>?) {
        if(numList == null){
            mProgressBar.visibility = View.GONE
            ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), root_layout_login, ApplicationConstants.ACTION_OK)
            return
        }else{
            parseResponse(numList)
        }
    }

    private fun parseResponse(response: ArrayList<String>) {
        for(numbers in response) {
            if(numbers == mobile){
                isExists = true
            }
        }
        if(isExists) {
            doLogin()
        }else{
            mProgressBar.visibility = View.GONE
            ApplicationUtility.showDialog(this@Login, this.resources.getString(R.string.new_user), this.resources.getString(R.string.num_no_exist), this as TaskOnComplete, ApplicationConstants.EMPTY)
        }
    }

    fun showOTPDialog(verificationId: String, context: Activity) {
        startTimerForOTP()
        val mBottomSheetDialog = BottomSheetDialog(context)
        val sheetView = context.layoutInflater.inflate(R.layout.otp_layout, null)
        mBottomSheetDialog.setCancelable(false)
        mBottomSheetDialog.setContentView(sheetView)
        mBottomSheetDialog.show()
        loginButton = sheetView.findViewById(R.id.verify_button)
        loginButton.typeface = ApplicationUtility.fontBold(context)
        loginButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        val otpInput = sheetView.findViewById<TextInputEditText>(R.id.otp_input)
        cancelButton = sheetView.findViewById(R.id.cancel_button)
        cancelButton.typeface = ApplicationUtility.fontBold(context)
        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        val resendImg = sheetView.findViewById<ImageView>(R.id.resend_image)
        resendText = sheetView.findViewById(R.id.resend_text)
        otp = otpInput.text.toString().trim()
        if(otp.isEmpty()){
            loginButton.isEnabled = false
        }
        resendText.setOnClickListener {
            if (!isOTPAuthenticationInProgress) {
                authenticateUser()
                startTimerForOTP()
            }
        }
        resendImg.setOnClickListener {
            if (!isOTPAuthenticationInProgress) {
                authenticateUser()
                startTimerForOTP()
            }
        }
        cancelButton.setOnClickListener {
            myCountDownTimer.cancelTimer()
        }
        loginButton.setOnClickListener {
            loginProgressBar.visibility = View.VISIBLE
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            firebaseServices.signInWithPhoneAuthCredential(credential, context)
        }
    }

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            isOTPAuthenticationInProgress = false
            loginButton.isEnabled = false
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            resendText.text = "Click to resend in ${(millisUntilFinished / 1000)} seconds"
        }

        fun cancelTimer() {
            isOTPAuthenticationInProgress = false
            super.cancel()
        }
    }

    private fun startTimerForOTP() {
        myCountDownTimer.start()
    }

    private fun authenticateUser() {
        if(mProgressBar.visibility == View.GONE){
            mProgressBar.visibility = View.VISIBLE
        }
        mobileNumber = ApplicationConstants.COUNTRY_CODE + mobile
        firebaseServices.setListener(this as TaskOnComplete)
        firebaseServices.authenticateUser(this, mobileNumber, ApplicationConstants.EMPTY)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbarTitle.text = getString(R.string.login)
        toolbarTitle.typeface = ApplicationUtility.fontBold(this)
        toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun setupUI() {
        mobileNum.typeface = ApplicationUtility.fontRegular(this)
        mobileNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        nextButton.typeface = ApplicationUtility.fontBold(this)
        nextButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_LARGE)

        regLabel.typeface = ApplicationUtility.fontRegular(this)
        regLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        signUp.typeface = ApplicationUtility.fontBold(this)
        signUp.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
        System.exit(0)
    }
}
