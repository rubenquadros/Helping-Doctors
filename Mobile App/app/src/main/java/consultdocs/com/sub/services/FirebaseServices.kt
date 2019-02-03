package consultdocs.com.sub.services

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import consultdocs.com.sub.bean.UserData
import consultdocs.com.sub.activity.Login
import consultdocs.com.sub.activity.Registration
import consultdocs.com.sub.bean.DoctorData
import consultdocs.com.sub.utility.*
import java.util.concurrent.TimeUnit

class FirebaseServices {

    private val mFirebaseInstance: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var mListener: TaskOnComplete
    private lateinit var userListner: GotUserData
    private lateinit var docListner: GotDoctors
    private lateinit var numListner: GotNumbers
    private lateinit var mUser: UserData
    private var mDoctors = ArrayList<DoctorData>()
    private lateinit var docData: DoctorData
    private lateinit var cat: String

    fun saveUser(path: String, userData: UserData, newUser: String, oldPath: String) {
        val postListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                mListener.onResponseReceived(ApplicationConstants.TASK_CANCELLED)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mUser = dataSnapshot.getValue(UserData::class.java)
                if (mUser != null) {
                    if(newUser == ApplicationConstants.NEW_PROFILE) {
                        mListener.onResponseReceived(ApplicationConstants.SUCCESS_RESP)
                    }else{
                        mFirebaseInstance.getReference(ApplicationConstants.USERS).child(oldPath).removeValue()
                        mListener.onResponseReceived(ApplicationConstants.UPDATE_PROF_SUCCESS)
                    }
                } else {
                    mListener.onResponseReceived(ApplicationConstants.FAIL_RESP)
                }
            }

        }
        val mFirebaseDatabaseReference: DatabaseReference = mFirebaseInstance.getReference(path)
        mFirebaseDatabaseReference.child(userData.msisdn!!).setValue(userData)
        mFirebaseDatabaseReference.addValueEventListener(postListener)
    }

    fun authenticateUser(context: Activity, mobileNum: String, service: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mobileNum,
            ApplicationConstants.OTP_TIME,
            TimeUnit.SECONDS,
            context,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
                    mListener.onResponseReceived(ApplicationConstants.AUTH_SUCCESS)
                    signInWithPhoneAuthCredential(p0!!, context)
                }

                override fun onVerificationFailed(p0: FirebaseException?) {
                    mListener.onResponseReceived(ApplicationConstants.CREATE_AUTH_FAIL)
                }

                override fun onCodeSent(verificationID: String?, token: PhoneAuthProvider.ForceResendingToken?) {
                    mListener.onResponseReceived(ApplicationConstants.CODE_SENT)
                    super.onCodeSent(verificationID, token)
                    if(service == ApplicationConstants.REGISTRATION) {
                        val registration = Registration()
                        registration.showOTPDialog(verificationID!!, context)
                    }else{
                        val login = Login()
                        login.showOTPDialog(verificationID!!, context)
                    }
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String?) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    mListener.onResponseReceived(ApplicationConstants.CODE_TIME_OUT)
                }
            }
        )
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, context: Activity){
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(context) {task ->
            if(task.isSuccessful){
                mListener.onResponseReceived(ApplicationConstants.CREATE_AUTH_SUCCESS)
            }else{
                mListener.onResponseReceived(ApplicationConstants.CREATE_AUTH_FAIL)
            }
        }
    }

    fun checkNumber() {
        val postListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                numListner.onNumbersReceived(null)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mKeys = ArrayList<String>()
                val mChildren = dataSnapshot.children
                for(keys in mChildren) {
                    mKeys.add(keys.key!!)
                }
                numListner.onNumbersReceived(mKeys)
            }
        }
        val mFirebaseDatabaseReference: DatabaseReference = mFirebaseInstance.getReference(ApplicationConstants.USERS)
        mFirebaseDatabaseReference.addValueEventListener(postListener)
    }

    fun getUserDetails(path: String){
        val postListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                userListner.receivedData(null)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(userSnapshot: DataSnapshot in dataSnapshot.children){
                    mUser = userSnapshot.getValue(UserData::class.java)!!
                }
                userListner.receivedData(mUser)
            }
        }
        val mFirebaseDatabaseReference: DatabaseReference = mFirebaseInstance.getReference(ApplicationConstants.USERS)
        mFirebaseDatabaseReference.child(path)
        mFirebaseDatabaseReference.addValueEventListener(postListener)
    }

    fun getCategories() {
        val postListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                mListener.onResponseReceived(ApplicationConstants.FAIL_RESP)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot: DataSnapshot in dataSnapshot.children) {
                    cat = userSnapshot.value.toString()
                }
                mListener.onResponseReceived(cat)
            }
        }
        val mFirebaseDatabaseReference: DatabaseReference = mFirebaseInstance.getReference(ApplicationConstants.DOC_CATEGORIES)
        mFirebaseDatabaseReference.addValueEventListener(postListener)
    }

    fun upDateUserEmail(path: String, newEmail: String) {
        val postListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                mListener.onResponseReceived(ApplicationConstants.UPDATE_EMAIL_FAIL)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mUser = dataSnapshot.getValue(UserData::class.java)
                if (mUser != null) {
                    mListener.onResponseReceived(ApplicationConstants.UPDATE_EMAIL_SUCCESS)
                } else {
                    mListener.onResponseReceived(ApplicationConstants.UPDATE_EMAIL_FAIL)
                }
            }
        }
        val mFirebaseDatabaseReference: DatabaseReference = mFirebaseInstance.getReference(ApplicationConstants.USERS)
        val basePath = mFirebaseDatabaseReference.child(path)
        basePath.child(ApplicationConstants.DATABASE_EMAIL).setValue(newEmail)
        mFirebaseDatabaseReference.addValueEventListener(postListener)
    }

    fun getDoctors(path: String) {
        val postListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                docListner.onDoctorsRetrieved(null)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val child = dataSnapshot.child(path)
                for(doctorData in child.children){
                    docData = doctorData.getValue(DoctorData::class.java)!!
                    mDoctors.add(docData)
                }
                docListner.onDoctorsRetrieved(mDoctors)
            }
        }
        val mFirebaseDatabaseReference: DatabaseReference = mFirebaseInstance.getReference(ApplicationConstants.DOCTORS)
        mFirebaseDatabaseReference.child(path)
        mFirebaseDatabaseReference.addValueEventListener(postListener)
    }

    fun setListener(listener: TaskOnComplete) {
        mListener = listener
    }

    fun setUserListener(listener: GotUserData) {
        userListner = listener
    }

    fun setDoctorsListener(listner: GotDoctors) {
        docListner = listner
    }

    fun setNumListner(listener: GotNumbers) {
        numListner = listener
    }
}

