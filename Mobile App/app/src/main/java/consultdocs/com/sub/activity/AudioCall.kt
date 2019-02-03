package consultdocs.com.sub.activity

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.util.TypedValue
import android.view.View
import consultdocs.com.sub.R
import consultdocs.com.sub.services.AgoraServices
import consultdocs.com.sub.utility.ApplicationConstants
import consultdocs.com.sub.utility.ApplicationUtility
import consultdocs.com.sub.utility.TaskOnComplete
import io.agora.AgoraAPIOnlySignal
import io.agora.IAgoraAPI
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import kotlinx.android.synthetic.main.activity_audio_call.*

class AudioCall : AppCompatActivity(), AgoraServices.OnAgoraEngineInterface, TaskOnComplete {

    private var mAgoraAPI: AgoraAPIOnlySignal? = null
    private var mRtcEngine: RtcEngine? = null
    private var mPlayer = MediaPlayer()
    private lateinit var mChannelID: String
    private lateinit var mAccount: String
    private var onCall = false
    private var onSpeaker = false
    private var onMute = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_call)
        setupUI()
        checkPermissions()
        muteCall.setOnClickListener {
            if(!onMute) {
                muteCall.background = this.resources.getDrawable(R.drawable.circle_img)
                mRtcEngine!!.muteLocalAudioStream(true)
                onMute = true
            }else{
                muteCall.background = this.resources.getDrawable(R.drawable.circle_img_secondary)
                mRtcEngine!!.muteLocalAudioStream(false)
                onMute = false
            }
        }
        speakerCall.setOnClickListener {
            if(!onSpeaker) {
                speakerCall.background = this.resources.getDrawable(R.drawable.circle_img)
                mRtcEngine!!.setEnableSpeakerphone(true)
                onSpeaker = true
            }else{
                speakerCall.background = this.resources.getDrawable(R.drawable.circle_img_secondary)
                mRtcEngine!!.setEnableSpeakerphone(false)
                onSpeaker = false
            }
        }
        endCall.setOnClickListener {
            chronometer.stop()
            if(onCall) {
                stopCall()
            }else{
                onEnd()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        if(checkSelfPermission(ApplicationConstants.permissions[0]) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(ApplicationConstants.permissions[1]) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(ApplicationConstants.permissions[2]) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(ApplicationConstants.permissions[3]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@AudioCall, ApplicationConstants.permissions, ApplicationConstants.REQUEST_CODE)
        }else{
            initializeAgora()
            startCall()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            ApplicationConstants.REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED){
                    ApplicationUtility.showSnack(this.resources.getString(R.string.permission_req), root_layout_audio_call, ApplicationConstants.ACTION_OK, this as TaskOnComplete, ApplicationConstants.EMPTY)
                }else{
                    initializeAgora()
                    startCall()
                }
            }
        }
    }

    private fun stopCall() {
        if(mAgoraAPI != null) {
            mAgoraAPI!!.channelInviteEnd(mChannelID, mAccount, 0)
        }
    }

    private fun callInRefuse() {
        if(mAgoraAPI != null) {
            mAgoraAPI!!.channelInviteRefuse(mChannelID, mAccount, 0, "{\"status\":0}")
        }
    }

    override fun onResume() {
        super.onResume()
        addCallBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayer.isPlaying) {
            mPlayer.stop()
        }


        if (mRtcEngine != null) {
            mRtcEngine!!.leaveChannel()
        }
        mRtcEngine = null

    }

    private fun startCall() {
        if(mAgoraAPI == null || mRtcEngine == null){
            goBack()
            return
        }
        mAgoraAPI!!.queryUserStatus("1234")
    }

    private fun onEnd() {
        chronometer.stop()
        finish()
    }

    private fun addCallBack() {
        mAgoraAPI!!.callbackSet(
            object : IAgoraAPI.ICallBack {

            override fun onChannelQueryUserIsIn(p0: String?, p1: String?, p2: Int) {

            }

            override fun onMsg(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInviteFailed(p0: String?, p1: String?, p2: Int, p3: Int, p4: String?) {
                runOnUiThread {
                    onCall = false
                }
            }

            override fun onInviteMsg(p0: String?, p1: String?, p2: Int, p3: String?, p4: String?, p5: String?) {

            }

            override fun onLogout(p0: Int) {
                if (p0 == IAgoraAPI.ECODE_LOGOUT_E_KICKED) {
                    ApplicationUtility.showToast(this@AudioCall, this@AudioCall.resources.getString(R.string.agora_login_other))
                    return
                } else if (p0 == IAgoraAPI.ECODE_LOGOUT_E_NET) {
                    ApplicationUtility.showToast(this@AudioCall, this@AudioCall.resources.getString(R.string.agora_login_fail))
                    onEnd()
                }
                onEnd()
            }

            override fun onChannelUserLeaved(p0: String?, p1: Int) {

            }

            override fun onChannelAttrUpdated(p0: String?, p1: String?, p2: String?, p3: String?) {

            }

            override fun onLoginFailed(p0: Int) {

            }

            override fun onUserAttrResult(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInviteAcceptedByPeer(channelID: String?, account: String?, uid: Int, p3: String?) {
                runOnUiThread {
                    if(mPlayer.isPlaying) {
                        mPlayer.stop()
                        chronometer.base = SystemClock.elapsedRealtime()
                    }
                    onCall = true
                    chronometer.visibility = View.VISIBLE
                    chronometer.start()
                }
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

            override fun onQueryUserStatusResult(name: String?, status: String?) {
                runOnUiThread {
                    if(status == ApplicationConstants.USER_ONLINE) {
                        try {
                            mPlayer = MediaPlayer.create(this@AudioCall, R.raw.call_out)
                            mPlayer.isLooping = true
                            mPlayer.start()
                        }catch (e: Exception){
                            //media player exception
                        }
                        val channelName = ApplicationUtility.readValue(this@AudioCall, ApplicationConstants.MSISDN) + "1234"
                        mAgoraAPI!!.channelInviteUser(channelName, "1234", 0)
                    }else if(status == ApplicationConstants.USER_OFFLINE){
                        ApplicationUtility.showToast(this@AudioCall, this@AudioCall.resources.getString(R.string.user_offline))
                        onEnd()
                    }
                }
            }

            override fun onMessageChannelReceive(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onInviteReceived(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onLog(p0: String?) {

            }

            override fun onInviteReceivedByPeer(channelID: String?, account: String?, uid: Int) {
                runOnUiThread {
                    onCall = true
                    mChannelID = channelID!!
                    mAccount = account!!
                    setAudioProfile()
                    joinChannel()
                }
            }

            override fun onDbg(p0: String?, p1: ByteArray?) {

            }

            override fun onInviteRefusedByPeer(channelID: String?, account: String?, uid: Int, p3: String?) {
                runOnUiThread {
                    if(mPlayer.isPlaying){
                        mPlayer.stop()
                    }
                    if (p3!!.contains("status") && p3.contains("1")) {
                        ApplicationUtility.showToast(this@AudioCall, "$account is busy")
                    } else {
                        ApplicationUtility.showToast(this@AudioCall, "$account has rejected your call")
                    }
                    onCall = false
                    onEnd()
                }

            }

            override fun onInviteEndByPeer(channelName: String?, account: String?, uid: Int, p3: String?) {
                runOnUiThread {
                    if(channelName.equals(mChannelID)){
                        onCall = false
                        onEnd()
                    }

                }
            }

            override fun onChannelJoined(p0: String?) {

            }

            override fun onInviteEndByMyself(channelID: String?, account: String?, uid: Int) {
                runOnUiThread {
                    onEnd()
                }
            }

            override fun onLoginSuccess(p0: Int, p1: Int) {

            }

            override fun onMessageSendSuccess(p0: String?) {

            }

            override fun onMessageInstantReceive(p0: String?, p1: Int, p2: String?) {

            }

            override fun onChannelUserJoined(p0: String?, p1: Int) {

            }

            override fun onChannelQueryUserNumResult(p0: String?, p1: Int, p2: Int) {
            }

            override fun onError(p0: String?, p1: Int, p2: String?) {

            }

            override fun onChannelLeaved(p0: String?, p1: Int) {

            }

            override fun onMessageSendProgress(p0: String?, p1: String?, p2: String?, p3: String?) {

            }

        })
    }

    private fun setAudioProfile() {
        mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        mRtcEngine!!.enableAudio()
        mRtcEngine!!.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY, Constants.AUDIO_SCENARIO_SHOWROOM)
        mRtcEngine!!.enableLocalAudio(true)
        mRtcEngine!!.muteLocalAudioStream(false)
        mRtcEngine!!.muteRemoteAudioStream(0, false)
        mRtcEngine!!.muteAllRemoteAudioStreams(false)
        mRtcEngine!!.setDefaultMuteAllRemoteAudioStreams(false)
        mRtcEngine!!.setDefaultAudioRoutetoSpeakerphone(false)
    }


    private fun joinChannel() {
        mRtcEngine!!.joinChannel(null, mChannelID, "Extra Optional Data", 0)
    }

    private fun initializeAgora() {
        mAgoraAPI = AgoraServices.the().getmAgoraAPI()
        mRtcEngine = AgoraServices.the().getmRtcEngine()
        AgoraServices.the().setOnAgoraEngineInterface(this as AgoraServices.OnAgoraEngineInterface)
    }

    private fun goBack() {
        onEnd()
        onBackPressed()
    }

    override fun onBackPressed() {
        stopCall()
        super.onBackPressed()
    }

    private fun setupUI() {
        chronometer.typeface = ApplicationUtility.fontRegular(this)
        chronometer.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResponseReceived(response: String) {
        when(response) {
            ApplicationConstants.SNACKBAR_ACTION -> {
                checkPermissions()
            }
            else -> {
                ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), root_layout_audio_call, ApplicationConstants.ACTION_OK)
                return
            }
        }
    }


    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {

    }

    override fun onUserOffline(uid: Int, reason: Int) {

    }

    override fun onUserMuteVideo(uid: Int, muted: Boolean) {
    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        callInRefuse()
    }
}