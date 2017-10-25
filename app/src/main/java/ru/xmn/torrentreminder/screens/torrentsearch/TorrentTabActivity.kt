package ru.xmn.torrentreminder.screens.torrentsearch

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_torrent_tab.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.xmn.common.extensions.hideKeyboard
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.ScheduledJobService
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.NavigateActivity
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches.SavedSearchesFragment
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.search.SearchFragment
import android.content.Intent
import ru.xmn.common.extensions.log


class TorrentTabActivity : AppCompatActivity(), NavigateActivity {
    private val list: List<Fragment> = listOf(SearchFragment(), SavedSearchesFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torrent_tab)
        setupToolbar()
        setupViewPager()
        ScheduledJobService.scheduleJob(applicationContext)
        processIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        processIntent(intent)
    }

    private fun processIntent(intent: Intent) {
        if (intent.hasExtra(ScheduledJobService.INTENT_KEY)) {
            val updatedList = intent.getStringArrayListExtra(ScheduledJobService.INTENT_KEY)
            if (updatedList.size == 1)
                Handler().postDelayed({gotoSavedSearch(updatedList[0])}, 200)
            else
                gotoSavedSearchList()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
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

                if (currentFragment !is SavedSearchesFragment) {
                    Handler().postDelayed({ fragmentList.filterIsInstance(SavedSearchesFragment::class.java).first().deleteNewSearch() }, 300)
                }
            }
        })
    }

    override fun gotoSavedSearch(query: String) {
        viewPager.setCurrentItem(0, true)
        (viewPager.adapter as TabAdapter).updateQuery(query)
    }

    override fun gotoSavedSearchList() {
        viewPager.setCurrentItem(1, true)
    }

}