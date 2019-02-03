package consultdocs.com.sub.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import consultdocs.com.sub.R
import consultdocs.com.sub.custom.HomeAdapter
import kotlinx.android.synthetic.main.activity_home_screen.*
import kotlinx.android.synthetic.main.content_home_screen.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import android.widget.ProgressBar
import consultdocs.com.sub.bean.UserData
import consultdocs.com.sub.custom.CustomNavDrawerFont
import consultdocs.com.sub.services.FirebaseServices
import consultdocs.com.sub.utility.ApplicationConstants
import consultdocs.com.sub.utility.ApplicationUtility
import consultdocs.com.sub.utility.GotUserData
import consultdocs.com.sub.utility.TaskOnComplete
import kotlinx.android.synthetic.main.nav_header_home_screen.*
import java.util.*
import kotlin.collections.ArrayList

class HomeScreen : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, GotUserData, TaskOnComplete {

    private var isHidden: Boolean = true
    private lateinit var age: String
    private lateinit var mProgressBar: ProgressBar
    private lateinit var docCategories: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        mProgressBar = findViewById(R.id.homeProgressBar)
        setupToolbar()
        setupUI()
        retrieveDocCategories()
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val layoutManager = LinearLayoutManager(this)
        homeRecyclerView.layoutManager = layoutManager

        selfButton.setOnClickListener {
            if(!isHidden){
                linearLayout.visibility = View.GONE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    selfButton.background = this.getDrawable(R.drawable.button_background)
                    selfButton.setTextColor(this.resources.getColor(R.color.colorWhite))
                    othersButton.background = this.getDrawable(R.drawable.button_secondary)
                    othersButton.setTextColor(this.resources.getColor(R.color.colorBlack))
                }
                isHidden = true
            }
        }

        othersButton.setOnClickListener {
            if(isHidden){
                linearLayout.visibility = View.VISIBLE
                isHidden = false
                age = age_et.text.toString().trim()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    othersButton.background = this.getDrawable(R.drawable.button_background)
                    othersButton.setTextColor(this.resources.getColor(R.color.colorWhite))
                    selfButton.background = this.getDrawable(R.drawable.button_secondary)
                    selfButton.setTextColor(this.resources.getColor(R.color.colorBlack))
                }
            }
        }
    }

    private fun retrieveDocCategories() {
        val firebaseServices = FirebaseServices()
        firebaseServices.setListener(this as TaskOnComplete)
        firebaseServices.getCategories()
    }

    private fun retrieveUserInfo() {
        mProgressBar.visibility = View.VISIBLE
        val mFirebaseServices = FirebaseServices()
        mFirebaseServices.setUserListener(this as GotUserData)
        val msisdn = ApplicationUtility.readValue(this, ApplicationConstants.MSISDN)
        if(msisdn == ApplicationConstants.EMPTY){
            mProgressBar.visibility = View.GONE
            ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), drawer_layout, ApplicationConstants.ACTION_OK)
            return
        }
        mFirebaseServices.getUserDetails(msisdn)
    }

    private fun applyFontToItem(menuItem: MenuItem) {
        val font = Typeface.createFromAsset(assets, ApplicationConstants.FONT_REGULAR)
        val mTitle = SpannableString(menuItem.title)
        mTitle.setSpan(CustomNavDrawerFont("", font), 0, mTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        menuItem.title = mTitle
    }

    private fun setupUI() {
        val menu = nav_view.menu
        for (i in 0 until menu.size()) {
            val mi = menu.getItem(i)

            //for applying a font to subMenu ...
            val subMenu = mi.subMenu
            if (subMenu != null && subMenu.size() > 0) {
                for (j in 0 until subMenu.size()) {
                    val subMenuItem = subMenu.getItem(j)
                    applyFontToItem(subMenuItem)
                }
            }
            //the method we have create in activity
            applyFontToItem(mi)
        }

        age_tv.typeface = ApplicationUtility.fontRegular(this)
        age_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        age_et.typeface = ApplicationUtility.fontRegular(this)
        age_et.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        patientLabel.typeface = ApplicationUtility.fontRegular(this)
        patientLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)

        selfButton.typeface = ApplicationUtility.fontBold(this)
        selfButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_MEDIUM)

        othersButton.typeface = ApplicationUtility.fontBold(this)
        othersButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_MEDIUM)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbarTitle.text = getString(R.string.home)
        toolbarTitle.typeface = ApplicationUtility.fontBold(this)
        toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this, Profile::class.java))
            }
            R.id.nav_history -> {
                // show medical history
            }
            R.id.nav_logout -> {
                ApplicationUtility.doLogout(this@HomeScreen)
            }
            R.id.nav_share -> {
                // share app!
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return false
    }



    override fun onResponseReceived(response: String) {
        when(response) {
            ApplicationConstants.FAIL_RESP -> {
                mProgressBar.visibility = View.GONE
                ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), drawer_layout, ApplicationConstants.ACTION_OK)
                return
            }
            else -> {
                docCategories = response
                retrieveUserInfo()
            }
        }
    }

    override fun receivedData(response: UserData?) {
        mProgressBar.visibility = View.GONE
        if(response == null){
            ApplicationUtility.showSnack(this.resources.getString(R.string.generic_error), drawer_layout, ApplicationConstants.ACTION_OK)
            return
        }
        updateAdapter(docCategories)
        name_tv.typeface = ApplicationUtility.fontBold(this)
        name_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        name_tv.text = response.name
        mobile_tv.typeface = ApplicationUtility.fontBold(this)
        mobile_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationConstants.FONT_REGULAR_SIZE)
        mobile_tv.text = response.msisdn
    }

    private fun updateAdapter(categories: String) {
        val catArray = Arrays.asList(categories.split(","))
        val homeAdapter = HomeAdapter(this, catArray[0] as ArrayList<String>)
        homeRecyclerView.adapter = homeAdapter
    }
}
