package ru.xmn.torrentreminder.screens.torrentsearch

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_torrent_search_list.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.xmn.common.extensions.hideKeyboard
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.search.SearchFragment
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches.SavedSearchesFragment


class TorrentTabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torrent_search_list)
        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
    }

    private fun setupViewPager() {
        val fragmentList = listOf<Fragment>(SearchFragment(), SavedSearchesFragment())
        val adapter = TabAdapter(supportFragmentManager, fragmentList, listOf<String>("Поиск", "Сохраненные поиски"))
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                viewPager.hideKeyboard()
                val currentFragment = fragmentList[position]
                if (currentFragment !is SavedSearchesFragment)
                    Handler().postDelayed({fragmentList.filterIsInstance(SavedSearchesFragment::class.java).first().deleteNewSearch()}, 300)
            }

        })
    }
}