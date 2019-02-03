package consultdocs.com.sub.utility

class ApplicationConstants {
    companion object {
        val permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val FONT_BOLD = "Montserrat-Bold.ttf"
        val FONT_REGULAR = "Montserrat-Regular.ttf"
        val FONT_BOLD_SIZE = 18F
        val FONT_REGULAR_SIZE = 18F
        val NAV_FONT_SIZE = 48F
        val FONT_LARGE = 22F
        val FONT_MEDIUM = 20F
        val COUNTRY_CODE = "+91"
        val ACTION_OK = "OK"
        val TAG = "consultDocs"
        val MY_PREFS_NAME = "consultDocs"
        val SUCCESS_RESP = "Success"
        val FAIL_RESP = "Failed"
        val CREATE_AUTH_FAIL = "Could not create Authentication"
        val EMPTY = "NA"
        val USERS = "Users"
        val MSISDN = "Mobile Number"
        val OTP_TIME = 60L
        val CODE_SENT = "Code Sent"
        val CODE_TIME_OUT = "Code Auto-retrieval time-out"
        val TASK_CANCELLED = "On Cancelled"
        val AUTH_SUCCESS = "Successful Authentication"
        val CREATE_AUTH_SUCCESS = "Successfully created Authentication"
        val NO_GENDER = "No Gender Selected"
        val EMPTY_FIELDS = "Empty Fields"
        val FUTURE_DATE = "Future Date"
        val EMAIL_REGEX = "[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"
        val EMAIL_NOMATCH = "Not a valid Email"
        val MSISDN_REGEX = "^[0-9]{10}\$"
        val MSISDN_NOMATCH = "Not a valid 10 digit number"
        val REGISTRATION = "Registration"
        val LOGIN = "Login"
        val SPLASH_TIME = 3000L
        val DOC_CATEGORIES = "DoctorCategories"
        val POSITIVE_BUTTON = "YES"
        val NEGATIVE_BUTTON = "NO"
        val DATABASE_MSISDN = "msisdn"
        val DATABASE_EMAIL = "email"
        val UPDATE_PROF_FAIL = "Update profile failed"
        val UPDATE_PROF_SUCCESS = "Update profile success"
        val UPDATE_EMAIL_SUCCESS = "Update email success"
        val UPDATE_EMAIL_FAIL = "Update email failed"
        val NO_RATING = "-"
        val NEW_PROFILE = "New User"
        val SNACKBAR_ACTION = "Snack bar button clicked"
        val DOCTOR_CATEGORY = "Doc category"
        val NUM_EDITED = "Number edited"
        val NUM_EDITED_GO = "Change"
        val NUM_EDITED_NOGO = "Don't change"
        val GOTO_LOGIN = "Logout"
        val DOCTORS = "doctors"
        val DOCTOR_NAME = "Doctor name"
        val DOC_FROM_TIME = "Available from time"
        val DOC_TO_TIME = "Available to time"
        val REQUEST_CODE = 101
        val USER_LEFT = "Remote user left"
        val USER_MUTED = "Remote user muted"
        val SPEAKER_ENABLED = "Speaker enabled"
        val USER_ONLINE = "1"
        val USER_OFFLINE = "0"
    }
}