package com.alim.snaploader

import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.alim.snaploader.Adapter.MainPagerAdapter
import com.alim.snaploader.BuildConfig
import com.alim.snaploader.Config.AppConfig
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.R
import com.alim.snaploader.Settings.OthersActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawer: DrawerLayout
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    lateinit var applicationData: ApplicationData

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationData = ApplicationData(this)
        if (applicationData.theme)
            setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.add)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val ver =  headerView.findViewById<TextView>(R.id.version)
        val ext =  headerView.findViewById<TextView>(R.id.extractor)
        val whatsNew =  headerView.findViewById<TextView>(R.id.whats)
        val themeSwitch = headerView.findViewById<SwitchCompat>(R.id.change_theme)

        if (extractorAvailable(this))
            ext.text = "Extension installed"
        else
            ext.text = "Extension not installed"

        ver.text = BuildConfig.VERSION_NAME
        themeSwitch.isChecked = applicationData.theme

        if (intent.getBooleanExtra("SESSION", false))
            drawer.openDrawer(GravityCompat.START)

        whatsNew.setOnClickListener {

        }
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            applicationData.theme = isChecked
            reCreate()
        }

        if (!applicationData.session) {
            applicationData.session = true
            if (!extractorAvailable(this))
                showFirst()
        }

        fab.setOnClickListener {
            if (extractorAvailable(this)) {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.add_dialog)
                val url = dialog.findViewById<TextInputEditText>(R.id.addr)
                dialog.findViewById<Button>(R.id.dismiss).setOnClickListener { dialog.dismiss() }
                dialog.findViewById<Button>(R.id.download).setOnClickListener {
                    download(url.text.toString())
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                showFirst()
                Toast.makeText(this, "Extractor required", Toast.LENGTH_SHORT).show()
            }
        }

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        tabLayout = findViewById(R.id.main_tab)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = MainPagerAdapter.ViewPagerAdapter(supportFragmentManager)

        tabLayout.addTab(tabLayout.newTab().setText("Home"))
        tabLayout.addTab(tabLayout.newTab().setText("Music"))
        tabLayout.addTab(tabLayout.newTab().setText("Sports"))
        tabLayout.addTab(tabLayout.newTab().setText("Gaming"))
        tabLayout.addTab(tabLayout.newTab().setText("News"))
        tabLayout.addTab(tabLayout.newTab().setText("Live"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
                Log.println(Log.ASSERT,"Tab Reselected","Reselected")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.println(Log.ASSERT,"Tab Unselected","Unselected")
            }
        })
    }

    private fun showFirst() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.extension_dialog)
        dialog.findViewById<TextView>(R.id.title).text = "Install Extension"
        dialog.findViewById<TextView>(R.id.des).text = resources.getString(R.string.install_des)
        dialog.findViewById<Button>(R.id.dismiss).setOnClickListener { dialog.dismiss() }
        val install = dialog.findViewById<Button>(R.id.download)
        install.text = "Install"
        install.setOnClickListener {
            val intent = Intent(this, OthersActivity::class.java)
            intent.putExtra("FIRST",true)
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun download(link: String) {
        val intent = Intent(this, ReceiverActivity::class.java)
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, link)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_downloads ->  startActivity(Intent(this, DownloadsActivity::class.java))
            R.id.nav_settings -> launchSettings()
            R.id.nav_share -> share()
            R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data!!.getStringExtra("THEME") == "CHANGE")
            reCreate()
    }

    private fun launchSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivityForResult(intent, 1)
    }

    private fun launchDownloads() {
        if (extractorAvailable(this)) {
            val intent = Intent()
            intent.putExtra(Intent.EXTRA_TEXT, applicationData.theme)
            intent.component = ComponentName(
                "com.alim.extractor",
                "com.alim.extractor.DownloadsActivity"
            )
            startActivity(intent)
        } else {
            val intent = Intent(this, OthersActivity::class.java)
            intent.putExtra("DIALOG",true)
            startActivity(intent)
            Toast.makeText(this, "Extension required.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractorAvailable(context: Context): Boolean {
        return try {
            context.packageManager.getApplicationInfo("com.alim.extractor", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun share() {
        shareApk()
    }

    private fun shareLink(){
        try {
            val sendBt = Intent(Intent.ACTION_SEND)
            sendBt.type = "text/plain"
            sendBt.putExtra(
                Intent.EXTRA_TEXT,
               AppConfig().shareUrl
            )
            startActivity(
                Intent.createChooser(
                    sendBt,
                    "Share it using"
                )
            )
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.println(Log.ASSERT, "Error", e1.toString())
        }
    }

    private fun shareApk() {
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val pm: PackageManager = packageManager
        val appInfo: ApplicationInfo
        try {
            appInfo = pm.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
            val sendBt = Intent(Intent.ACTION_SEND)
            sendBt.type = "application/vnd.android.package-archive"
            sendBt.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse("file://" + appInfo.publicSourceDir)
            )
            startActivity(
                Intent.createChooser(
                    sendBt,
                    "Share it using"
                )
            )
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.println(Log.ASSERT, "Error", e1.toString())
        }
    }

    private fun reCreate() {
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("SESSION", true)
            startActivity(intent)
            Objects.requireNonNull(overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            ))
            finish()
        },250)
    }
}