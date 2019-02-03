package consultdocs.com.sub.utility

import android.support.design.widget.Snackbar
import android.view.View
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.util.regex.Pattern
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v4.content.ContextCompat.startActivity
import android.util.DisplayMetrics
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import consultdocs.com.sub.activity.Login


class ApplicationUtility {
    companion object {
        fun showSnack(msg: String, view: View, action: String){
            val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction(action) {
                snackbar.dismiss()
            }
            snackbar.show()
        }

        fun showSnack(msg: String, view: View, action: String, listener: TaskOnComplete, service: String) {
            val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction(action) {
                if(service == ApplicationConstants.UPDATE_PROF_SUCCESS){
                    listener.onResponseReceived(ApplicationConstants.GOTO_LOGIN)
                    snackbar.dismiss()
                    return@setAction
                }
                listener.onResponseReceived(ApplicationConstants.SNACKBAR_ACTION)
                snackbar.dismiss()
            }
            snackbar.show()
        }

        fun showDialog(context: Activity, title: String, msg: String, listener: TaskOnComplete, service: String) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle(title)
            alertDialog.setMessage(msg)
            alertDialog.setPositiveButton(ApplicationConstants.POSITIVE_BUTTON) { _, _ ->
                if(service == ApplicationConstants.NUM_EDITED){
                    listener.onResponseReceived(ApplicationConstants.NUM_EDITED_GO)
                }else {
                    listener.onResponseReceived(ApplicationConstants.SUCCESS_RESP)
                }
            }
            alertDialog.setNegativeButton(ApplicationConstants.NEGATIVE_BUTTON) { _, _ ->
                if(service == ApplicationConstants.NUM_EDITED){
                    listener.onResponseReceived(ApplicationConstants.NUM_EDITED_NOGO)
                }else {
                    listener.onResponseReceived(ApplicationConstants.FAIL_RESP)
                }
            }
            alertDialog.show()
        }

        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        fun hideSoftKeyboard(activity: Activity) {
            val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            val focussedView = activity.currentFocus
            if (focussedView != null) {
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken, 0
                )
            }
        }

        fun regexValidator(pattern: Pattern, value: String): String{
            if(!pattern.matcher(value).matches()){
                return ApplicationConstants.FAIL_RESP
            }
            return ApplicationConstants.SUCCESS_RESP
        }

        fun storeValue(key: String, value: String, context: Activity) {
            val editor = context.getSharedPreferences(ApplicationConstants.MY_PREFS_NAME, MODE_PRIVATE).edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun readValue(context: Activity, key: String) : String{
            val pref = context.getSharedPreferences(ApplicationConstants.MY_PREFS_NAME, MODE_PRIVATE)
            return pref.getString(key, null) ?: ApplicationConstants.EMPTY
        }

        fun fontRegular(context: Context): Typeface {
            return Typeface.createFromAsset(context.assets, ApplicationConstants.FONT_REGULAR)
        }

        fun fontBold(context: Context): Typeface {
            return Typeface.createFromAsset(context.assets, ApplicationConstants.FONT_BOLD)
        }

        fun doLogout(context: Activity) {
            FirebaseAuth.getInstance().signOut()
            startActivity(context, Intent(context, Login::class.java), null)
        }

        fun deviceHeight(): Int {
            val displayMetrics = DisplayMetrics()
            return displayMetrics.heightPixels
        }

        fun deviceWidth(): Int {
            val displayMetrics = DisplayMetrics()
            return displayMetrics.widthPixels
        }
    }
}