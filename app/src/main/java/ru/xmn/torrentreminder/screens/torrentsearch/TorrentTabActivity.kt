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
import ru.xmn.torrentreminder.features.torrent.ScheduledJobService
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches.NavigateActivity
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches.SavedSearchesFragment
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.search.SearchFragment


class TorrentTabActivity : AppCompatActivity(), NavigateActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torrent_search_list)
        setupToolbar()
        setupViewPager()
        ScheduledJobService.scheduleJob(applicationContext)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
    }

    private val list: List<Fragment>
        get() {
            val fragmentList = listOf<Fragment>(SearchFragment(), SavedSearchesFragment())
            return fragmentList
        }

    private fun setupViewPager() {
        val fragmentList = list
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
                    Handler().postDelayed({ fragmentList.filterIsInstance(SavedSearchesFragment::class.java).first().deleteNewSearch() }, 300)
            }

        })
    }

    override fun gotoSavedSearch(query: String) {
        val searchFragmentIndex = 0
        viewPager.setCurrentItem(0, true)
        (list[searchFragmentIndex] as SearchFragment).setInitialQuery(query)
    }

}