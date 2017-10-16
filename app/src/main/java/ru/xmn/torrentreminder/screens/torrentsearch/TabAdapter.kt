package ru.xmn.torrentreminder.screens.torrentsearch

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ru.xmn.common.extensions.log
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.search.SearchFragment

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

    private var query: String? = null

    fun updateQuery(query: String) {
        log("updateQuery, query = $query")
        this.query = query
        notifyDataSetChanged()
    }

    override fun getItemPosition(o: Any?): Int {
        if (o is SearchFragment) {
            log("getItemPosition o = $o, query = $query")
            o.initialQuery = query?:""
            query = null
        }
        return super.getItemPosition(o)
    }
}