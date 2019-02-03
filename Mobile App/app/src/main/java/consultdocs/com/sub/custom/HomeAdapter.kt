package consultdocs.com.sub.custom

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import consultdocs.com.sub.R
import consultdocs.com.sub.activity.DisplayDoctors
import consultdocs.com.sub.utility.ApplicationConstants
import consultdocs.com.sub.utility.ApplicationUtility
import kotlinx.android.synthetic.main.home_screen_category.view.*

class HomeAdapter(val context: Context, val docCategories: ArrayList<String>): RecyclerView.Adapter<HomeAdapter.ViewHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HomeAdapter.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.home_screen_category, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return docCategories.size
    }

    override fun onBindViewHolder(p0: HomeAdapter.ViewHolder, p1: Int) {
        p0.categoryNames.typeface = ApplicationUtility.fontRegular(context)
        p0.categoryNames.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        p0.categoryNames.text = docCategories[p1]
        p0.categoryNames.setOnClickListener {
            val intent = Intent(context, DisplayDoctors::class.java)
            intent.putExtra(ApplicationConstants.DOCTOR_CATEGORY, docCategories[p1])
            context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val categoryNames = itemView.category_tv!!
    }
}