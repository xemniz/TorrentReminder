package ru.xmn.torrentreminder.screens.torrentsearch

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.firebase.jobdispatcher.*
import kotlinx.android.synthetic.main.activity_torrent_search_list.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.xmn.common.extensions.hideKeyboard
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.ScheduledJobService
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.search.SearchFragment
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches.SavedSearchesFragment


class TorrentTabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torrent_search_list)
        setupToolbar()
        setupViewPager()
        scheduleJob(applicationContext)
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

    private fun scheduleJob(context: Context){
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
        val job = createJob(dispatcher)
        dispatcher.mustSchedule(job)
    }

    private fun createJob(dispatcher: FirebaseJobDispatcher): Job {

        return dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(ScheduledJobService::class.java)
                .setTag("UpdateAllTorrentsItemEveryDay")
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(300, 86400))
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build()
    }

}