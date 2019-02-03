package consultdocs.com.sub.bean

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class DoctorData(
    @SerializedName("languagesKnown")
    var languagesKnown: String? = "",
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("fromTime")
    var fromTime: String? = "",
    @SerializedName("toTime")
    var toTime: String? = "",
    @SerializedName("userName")
    var userName: String? = null
) {
    constructor(): this("", "", "", "", "")
}