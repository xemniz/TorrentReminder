package ru.xmn.torrentreminder.screens.torrentsearch

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.torrent_list.*
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.TorrentSearch
import ru.xmn.torrentreminder.screens.torrentsearch.searchlist.TorrentSearchAdapter


class TorrentSearchActivity : AppCompatActivity() {
    lateinit var torrentSearchViewModel: TorrentSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.torrent_list)
        setupToolbar()
        setupClickListeners()
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        torrentSearchViewModel = ViewModelProviders.of(this).get(TorrentSearchViewModel::class.java)
        torrentSearchViewModel.torrentItemsLiveData.observe(this, Observer {
            when (it) {
                is TorrentSearchState.Loading -> showLoading()
                is TorrentSearchState.Success -> showValue(it.items)
                is TorrentSearchState.Error -> showError()
            }
        })
    }

    private fun showError() {

    }

    private fun showValue(items: List<TorrentSearch>) {
        (torrentItemsList.adapter as TorrentSearchAdapter).items = items
    }

    private fun showLoading() {
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupClickListeners() {
        fab.setOnClickListener { torrentSearchViewModel.startCreateNewSearch() }
    }

    private fun setupRecyclerView() {
        torrentItemsList.adapter = TorrentSearchAdapter({
            id, query -> torrentSearchViewModel.updateSearch(id, query)
        }, { torrentSearchViewModel.deleteItem(it) })
    }

}


