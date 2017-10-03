package ru.xmn.torrentreminder.screens.torrentsearch.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.TorrentSearchFragment
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.TorrentTrackFragment

class ActivityFragmentsAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {


    val fragmentList = listOf<Fragment>(TorrentSearchFragment(), TorrentTrackFragment())
    val fragmentTitleList = listOf<String>("Поиск","Сохраненные поиски")


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