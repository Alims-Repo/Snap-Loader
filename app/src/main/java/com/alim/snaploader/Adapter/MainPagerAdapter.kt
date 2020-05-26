package com.alim.snaploader.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.alim.snaploader.Fragment.*

class MainPagerAdapter {
    class ViewPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return HomeFragment()
                1 -> return MusicFragment()
                2 -> return SportsFragment()
                3 -> return GamingFragment()
                4 -> return NewsFragment()
                5 -> return LiveFragment()
            }
            return HomeFragment()
        }
        override fun getCount(): Int {
            return 6
        }
    }
}