package consultdocs.com.sub.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import android.widget.ProgressBar
import consultdocs.com.sub.R
import consultdocs.com.sub.bean.DoctorData
import consultdocs.com.sub.custom.DoctorsAdapter
import consultdocs.com.sub.services.FirebaseServices
import consultdocs.com.sub.utility.ApplicationConstants
import consultdocs.com.sub.utility.ApplicationUtility
import consultdocs.com.sub.utility.GotDoctors
import kotlinx.android.synthetic.main.activity_display_doctors.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class DisplayDoctors : AppCompatActivity(), GotDoctors{

    private val firebaseServices = FirebaseServices()
    private var fromTime = ArrayList<String>()
    private var toTime = ArrayList<String>()
    private var userName = ArrayList<String>()
    private var languages = ArrayList<String>()
    private var ratings = ArrayList<String>()
    private lateinit var mProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_doctors)
        mProgressBar = findViewById(R.id.docProgressBar)
        setupToolBar()
        val layoutManager = LinearLayoutManager(this)
        docRecyclerView.layoutManager = layoutManager
        getDoctors()
    }

    private fun getDoctors() {
        mProgressBar.visibility = View.VISIBLE
        firebaseServices.setDoctorsListener(this as GotDoctors)
        firebaseServices.getDoctors(intent.getStringExtra(ApplicationConstants.DOCTOR_CATEGORY))
    }

    override fun onDoctorsRetrieved(response: ArrayList<DoctorData>?) {
        mProgressBar.visibility = View.GONE
        if(response == null){
            ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), root_layout_disp_docs, ApplicationConstants.ACTION_OK)
            return
        }
        parseResponse(response)
    }

    private fun parseResponse(response: ArrayList<DoctorData>) {
        for(items in response) {
            fromTime.add(items.fromTime.toString())
            toTime.add(items.toTime.toString())
            userName.add(items.userName.toString())
            languages.add(items.languagesKnown.toString())
            ratings.add(ApplicationConstants.EMPTY)
        }
        updateAdapter(fromTime, toTime, userName, languages, ratings)
    }

    private fun updateAdapter(
        fromTime: ArrayList<String>,
        toTime: ArrayList<String>,
        userName: ArrayList<String>,
        languages: ArrayList<String>,
        ratings: ArrayList<String>
    ) {
        val docAdapter = DoctorsAdapter(this, userName, ratings, languages, fromTime, toTime)
        docRecyclerView.adapter = docAdapter
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        toolbarTitle.text = getString(R.string.doctors)
        toolbarTitle.typeface = ApplicationUtility.fontBold(this)
        toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        onBackPressed()
        return true
    }
}
