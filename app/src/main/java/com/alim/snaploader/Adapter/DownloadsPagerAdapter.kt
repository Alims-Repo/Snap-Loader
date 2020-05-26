package com.alim.snaploader.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.alim.snaploader.Fragment.Downloads.DownloadedFragment
import com.alim.snaploader.Fragment.Downloads.InterruptedFragment
import com.alim.snaploader.Fragment.Downloads.RunningFragment

class DownloadsPagerAdapter {
    class ViewPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return RunningFragment()
                1 -> return DownloadedFragment()
                2 -> return InterruptedFragment()
            }
            return RunningFragment()
        }
        override fun getCount(): Int {
            return 3
        }
    }
}