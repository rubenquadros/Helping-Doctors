package consultdocs.com.sub.services

import android.app.Application
import android.util.Log
import consultdocs.com.sub.R
import consultdocs.com.sub.utility.ApplicationConstants
import io.agora.AgoraAPIOnlySignal
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine


class AgoraServices : Application() {
    private lateinit var m_agoraAPI: AgoraAPIOnlySignal
    private lateinit var mRtcEngine: RtcEngine
    private var onAgoraEngineInterface: OnAgoraEngineInterface? = null

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface!!.onFirstRemoteVideoDecoded(uid, width, height, elapsed)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface!!.onUserOffline(uid, reason)
            }
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface!!.onUserMuteVideo(uid, muted)
            }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface!!.onJoinChannelSuccess(channel, uid, elapsed)
            }
        }

    }

    init {
        mInstance = this
    }

    override fun onCreate() {
        super.onCreate()

        setupAgoraEngine()
    }

    fun getmRtcEngine(): RtcEngine? {
        return mRtcEngine
    }

    fun getmAgoraAPI(): AgoraAPIOnlySignal? {
        return m_agoraAPI
    }

    private fun setupAgoraEngine() {
        val appID = getString(R.string.agora_app_id)

        try {
            m_agoraAPI = AgoraAPIOnlySignal.getInstance(this, appID)
            mRtcEngine = RtcEngine.create(baseContext, appID, mRtcEventHandler)
        } catch (e: Exception) {
            Log.e(ApplicationConstants.TAG, "NEED TO check rtc sdk init fatal error\n" + e.toString())
            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }

    }

    fun setOnAgoraEngineInterface(onAgoraEngineInterface: OnAgoraEngineInterface) {
        this.onAgoraEngineInterface = onAgoraEngineInterface
    }

    interface OnAgoraEngineInterface {
        fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int)

        fun onUserOffline(uid: Int, reason: Int)

        fun onUserMuteVideo(uid: Int, muted: Boolean)

        fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int)
    }

    companion object {
        private lateinit var mInstance: AgoraServices

        fun the(): AgoraServices {
            return mInstance
        }
    }
}
