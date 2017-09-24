package ru.xmn.torrentreminder.screens.torrentsearch

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.torrent_list.*
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.TorrentSearch
import ru.xmn.torrentreminder.screens.torrentsearch.searchlist.TorrentSearchAdapter


class TorrentSearchActivity : AppCompatActivity() {
    private lateinit var torrentSearchViewModel: TorrentSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.torrent_list)
        setupToolbar()
        setupViewModel()
        setupClickListeners()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        val torrentSearchActivity = this
        torrentSearchViewModel = ViewModelProviders.of(torrentSearchActivity).get(TorrentSearchViewModel::class.java).apply {
            torrentItemsLiveData.observe(torrentSearchActivity, Observer {
                showValue(it ?: emptyList<TorrentSearch>())
            })
            errorToastLiveData.observe(torrentSearchActivity, Observer {
                when (it) {
                    ToastMsg.NOTHING -> {
                    }
                    ToastMsg.UPDATING_ERROR -> {
                        showUpdatingError()
                        torrentSearchViewModel.toastIsViewed()
                    }
                }
            })
            showSwipeRefresh.observe(torrentSearchActivity, Observer {
                swipe_container.isRefreshing = it ?: false
            })
        }

    }

    private fun showUpdatingError() {
        Toast.makeText(applicationContext, "Не удалось обновить поиск", Toast.LENGTH_SHORT).show()
    }

    private fun showValue(items: List<TorrentSearch>) {
        (torrentItemsList.adapter as TorrentSearchAdapter).items = items
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupClickListeners() {
        fab.setOnClickListener { torrentSearchViewModel.createNewSearch() }
        swipe_container.setOnRefreshListener { torrentSearchViewModel.updateAllItems() }
    }

    private fun setupRecyclerView() {
        torrentItemsList.adapter = TorrentSearchAdapter({ id, query ->
            torrentSearchViewModel.updateSearch(id, query)
        }, { torrentSearchViewModel.deleteItem(it) })
    }

}


