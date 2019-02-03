package consultdocs.com.sub.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import consultdocs.com.sub.R
import consultdocs.com.sub.activity.ShowDoctor
import consultdocs.com.sub.utility.ApplicationConstants
import consultdocs.com.sub.utility.ApplicationUtility
import kotlinx.android.synthetic.main.doctors_layout.view.*

class DoctorsAdapter(val context: Context, val docNames: ArrayList<String>,
                     val docRatings: ArrayList<String>, val docLanguages: ArrayList<String>,
                     val availableFrom: ArrayList<String>, val availableTo: ArrayList<String>  ): RecyclerView.Adapter<DoctorsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.doctors_layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return docNames.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val from = availableFrom[p1]
        val to = availableTo[p1]
        p0.docName.typeface = ApplicationUtility.fontRegular(context)
        p0.docName.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        p0.docName.text = docNames[p1]
        p0.docRating.typeface = ApplicationUtility.fontRegular(context)
        p0.docRating.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        if(docRatings[p1] == ApplicationConstants.EMPTY){
            p0.docRating.text = ApplicationConstants.NO_RATING
            p0.ratings.visibility = View.GONE
        }else{
            p0.docRating.text = docRatings[p1]
        }
        p0.docLanguage.typeface = ApplicationUtility.fontRegular(context)
        p0.docLanguage.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        p0.docLanguage.text = docLanguages[p1]
        p0.docTiming.typeface = ApplicationUtility.fontRegular(context)
        p0.docTiming.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        p0.docTiming.text = "Available from $from to $to"
        p0.docLinearLayout.setOnClickListener {
            val intent = Intent(context, ShowDoctor::class.java)
            intent.putExtra(ApplicationConstants.DOCTOR_NAME, docNames[p1])
            intent.putExtra(ApplicationConstants.DOC_FROM_TIME, from)
            intent.putExtra(ApplicationConstants.DOC_TO_TIME, to)
            context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ratings = itemView.rating
        val docName = itemView.docName
        val docRating = itemView.docRating
        val docLanguage = itemView.docLanguage
        val docTiming = itemView.docTiming
        val docLinearLayout = itemView.docsLinearLayout
    }
}