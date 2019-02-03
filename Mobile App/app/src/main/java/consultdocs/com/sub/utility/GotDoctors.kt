package consultdocs.com.sub.utility

import consultdocs.com.sub.bean.DoctorData

interface GotDoctors {
    fun onDoctorsRetrieved(response: ArrayList<DoctorData>?)
}