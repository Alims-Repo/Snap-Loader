package com.alim.snaploader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.alim.snaploader.Adapter.DownloadsPagerAdapter
import com.alim.snaploader.Database.ApplicationData
import com.alim.snaploader.R
import com.google.android.material.tabs.TabLayout

class DownloadsActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ApplicationData(this).theme)
            setTheme(R.style.AppThemeDark)
        setContentView(R.layout.activity_downloads)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        tabLayout = findViewById(R.id.main_tab)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = DownloadsPagerAdapter.ViewPagerAdapter(supportFragmentManager)

        tabLayout.addTab(tabLayout.newTab().setText("Running"))
        tabLayout.addTab(tabLayout.newTab().setText("Downloads"))
        tabLayout.addTab(tabLayout.newTab().setText("Interrupted"))
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
}
