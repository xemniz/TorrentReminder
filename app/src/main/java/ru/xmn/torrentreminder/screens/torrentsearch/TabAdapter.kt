package ru.xmn.torrentreminder.screens.torrentsearch

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class TabAdapter(manager: FragmentManager, val fragmentList: List<Fragment>,
                 val fragmentTitleList: List<String>) : FragmentPagerAdapter(manager) {


    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitleList[position]
    }
}