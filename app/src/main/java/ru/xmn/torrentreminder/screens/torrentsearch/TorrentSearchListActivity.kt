package ru.xmn.torrentreminder.screens.torrentsearch

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_torrent_search_list.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.screens.torrentsearch.adapters.ActivityFragmentsAdapter


class TorrentSearchListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torrent_search_list)
        setupToolbar()
        setupViewPager(supportFragmentManager)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
    }

    private fun setupViewPager(manager: FragmentManager) {
        val adapter = ActivityFragmentsAdapter(manager)
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }
}