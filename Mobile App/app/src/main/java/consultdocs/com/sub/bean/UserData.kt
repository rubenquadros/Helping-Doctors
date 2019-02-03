package consultdocs.com.sub.bean

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class UserData(
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("msisdn")
    var msisdn: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("dob")
    var dob: String? = "",
    @SerializedName("gender")
    var gender: String? = ""
) {

    constructor() : this("","","","","")

}