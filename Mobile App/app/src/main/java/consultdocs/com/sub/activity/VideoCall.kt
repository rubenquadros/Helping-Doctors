package consultdocs.com.sub.activity

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
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
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtc.video.VideoEncoderConfiguration.STANDARD_BITRATE
import kotlinx.android.synthetic.main.activity_video_call.*

class VideoCall : AppCompatActivity(), AgoraServices.OnAgoraEngineInterface, TaskOnComplete {

    private var mAgoraAPI: AgoraAPIOnlySignal? = null
    private var mRtcEngine: RtcEngine? = null
    private var mPlayer = MediaPlayer()
    private lateinit var mChannelID: String
    private lateinit var mAccount: String
    private var onCall = false
    private var switchCamera = false
    private var onMute = false
    private var mRemoteUid = 0


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)
        checkPermissions()
        call_mute_button.setOnClickListener {
            if(!onMute) {
                call_mute_button.background = this.resources.getDrawable(R.drawable.circle_img)
                call_mute_button.setColorFilter(this.resources.getColor(R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN)
                mRtcEngine!!.muteLocalAudioStream(true)
                onMute = true
            }else{
                call_mute_button.background = this.resources.getDrawable(R.drawable.circle_img_secondary)
                call_mute_button.setColorFilter(this.resources.getColor(R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN)
                mRtcEngine!!.muteLocalAudioStream(false)
                onMute = false
            }
        }
        call_switch_camera.setOnClickListener {
            if(!switchCamera) {
                call_mute_button.setColorFilter(this.resources.getColor(R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN)
                call_switch_camera.background = this.resources.getDrawable(R.drawable.circle_img)
                mRtcEngine!!.switchCamera()
                switchCamera = true
            }else {
                call_switch_camera.background = this.resources.getDrawable(R.drawable.circle_img_secondary)
                call_mute_button.setColorFilter(this.resources.getColor(R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN)
                mRtcEngine!!.switchCamera()
                switchCamera = false
            }
        }
        call_button_hangup.setOnClickListener {
            goBack()
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

    override fun onBackPressed() {
        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        if(checkSelfPermission(ApplicationConstants.permissions[0]) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(ApplicationConstants.permissions[1]) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(ApplicationConstants.permissions[2]) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(ApplicationConstants.permissions[3]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@VideoCall, ApplicationConstants.permissions, ApplicationConstants.REQUEST_CODE)
        }else{
            initializeAgora()
            startVideoCall()
        }
    }

    private fun startVideoCall() {
        if(mAgoraAPI == null || mRtcEngine == null){
            goBack()
            return
        }
        mAgoraAPI!!.queryUserStatus("1234")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            ApplicationConstants.REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED){
                    ApplicationUtility.showSnack(this.resources.getString(R.string.permission_req), root_layout_video_call, ApplicationConstants.ACTION_OK, this as TaskOnComplete, ApplicationConstants.EMPTY)
                }else{
                    initializeAgora()
                    startVideoCall()
                }
            }
        }
    }

    private fun addCallBack() {
        mAgoraAPI!!.callbackSet(object : IAgoraAPI.ICallBack {
            override fun onChannelQueryUserIsIn(p0: String?, p1: String?, p2: Int) {

            }

            override fun onMsg(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInviteFailed(p0: String?, p1: String?, p2: Int, p3: Int, p4: String?) {

            }

            override fun onInviteMsg(p0: String?, p1: String?, p2: Int, p3: String?, p4: String?, p5: String?) {

            }

            override fun onLogout(p0: Int) {
                if (p0 == IAgoraAPI.ECODE_LOGOUT_E_KICKED) {
                    ApplicationUtility.showToast(this@VideoCall, this@VideoCall.resources.getString(R.string.agora_login_other))
                    return
                } else if (p0 == IAgoraAPI.ECODE_LOGOUT_E_NET) {
                    ApplicationUtility.showToast(this@VideoCall, this@VideoCall.resources.getString(R.string.agora_login_fail))
                    goBack()
                }
                goBack()
            }

            override fun onChannelUserLeaved(p0: String?, p1: Int) {

            }

            override fun onChannelAttrUpdated(p0: String?, p1: String?, p2: String?, p3: String?) {

            }

            override fun onLoginFailed(p0: Int) {

            }

            override fun onUserAttrResult(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInviteAcceptedByPeer(p0: String?, p1: String?, p2: Int, p3: String?) {
                runOnUiThread {
                    if (mPlayer.isPlaying) {
                        mPlayer.stop()
                    }
                    onCall = true
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
                            mPlayer = MediaPlayer.create(this@VideoCall, R.raw.call_out)
                            mPlayer.isLooping = true
                            mPlayer.start()
                        }catch (e: Exception){
                            //media player exception
                        }
                        val channelName = ApplicationUtility.readValue(this@VideoCall, ApplicationConstants.MSISDN) + "1234"
                        mAgoraAPI!!.channelInviteUser(channelName, "1234", 0)
                    }else if(status == ApplicationConstants.USER_OFFLINE){
                        ApplicationUtility.showToast(this@VideoCall, this@VideoCall.resources.getString(R.string.user_offline))
                        goBack()
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
                    setVideoProfile()
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
                        ApplicationUtility.showToast(this@VideoCall, "$account is busy")
                    } else {
                        ApplicationUtility.showToast(this@VideoCall, "$account has rejected your call")
                    }
                    onCall = false
                    goBack()
                }
            }

            override fun onInviteEndByPeer(channelName: String?, account: String?, uid: Int, p3: String?) {
                runOnUiThread {
                    if(channelName.equals(mChannelID)){
                        onCall = false
                        goBack()
                    }

                }
            }

            override fun onChannelJoined(p0: String?) {

            }

            override fun onInviteEndByMyself(p0: String?, p1: String?, p2: Int) {
                runOnUiThread {
                    goBack()
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

    private fun setupRemoteVideo(uid: Int) {
        if(remote_video_view_container.childCount >= 1) {
            remote_video_view_container.removeAllViews()
        }
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        remote_video_view_container.addView(surfaceView)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        remote_video_view_container.visibility = View.VISIBLE
    }

    private fun joinChannel() {
        mRtcEngine!!.joinChannel(null, mChannelID, "Extra Optional Data", 0)
    }

    private fun setVideoProfile() {
        val videoDimen = VideoEncoderConfiguration.VideoDimensions()
        videoDimen.height = ApplicationUtility.deviceHeight()
        videoDimen.width = ApplicationUtility.deviceWidth()
        val config = VideoEncoderConfiguration(videoDimen, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
            STANDARD_BITRATE, VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE)
        mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        //mRtcEngine!!.setVideoProfile(Constants.VIDEO_PROFILE_360P, false)
        mRtcEngine!!.setVideoEncoderConfiguration(config)
        mRtcEngine!!.muteLocalAudioStream(false)
        mRtcEngine!!.enableLocalAudio(true)
        mRtcEngine!!.muteRemoteAudioStream(0, false)
        mRtcEngine!!.muteLocalVideoStream(false)
        mRtcEngine!!.enableLocalVideo(true)
        mRtcEngine!!.muteRemoteVideoStream(0, false)
        mRtcEngine!!.enableVideo()
    }

    private fun initializeAgora() {
        mAgoraAPI = AgoraServices.the().getmAgoraAPI()
        mRtcEngine = AgoraServices.the().getmRtcEngine()
        AgoraServices.the().setOnAgoraEngineInterface(this as AgoraServices.OnAgoraEngineInterface)
    }

    private fun goBack() {
        finish()
        onBackPressed()
    }

    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        runOnUiThread(Runnable {
            if (mRemoteUid != 0) {
                return@Runnable
            }
            mRemoteUid = uid
            setupRemoteVideo(uid)
        })
    }

    override fun onUserOffline(uid: Int, reason: Int) {

    }

    override fun onUserMuteVideo(uid: Int, muted: Boolean) {

    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResponseReceived(response: String) {
        when(response) {
            ApplicationConstants.SNACKBAR_ACTION -> {
                checkPermissions()
            }
            else -> {
                ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), root_layout_video_call, ApplicationConstants.ACTION_OK)
                return
            }
        }
    }
}
