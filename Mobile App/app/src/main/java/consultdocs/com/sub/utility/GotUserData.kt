package consultdocs.com.sub.utility

import consultdocs.com.sub.bean.UserData

interface GotUserData {
    fun receivedData(response: UserData?)
}