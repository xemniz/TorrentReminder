package ru.xmn.torrentreminder.screens.torrentlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_torrent_list.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.domain.TorrentItem

class SavedSearchDetailsActivity : AppCompatActivity() {

    companion object {
        const val ID = "id"
    }

    lateinit var savedSearchDetailsViewModel: SavedSearchDetailsViewModule
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent.extras
        id = intent.getString(ID)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torrent_list)
        setupViewModel()
        setupRecyclerView()
        setupClickListener()
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupViewModel() {
        val torrentListActivity = this
        savedSearchDetailsViewModel = ViewModelProviders
                .of(this, SavedSearchDetailsViewModule.TorrentListViewModelFactory(id))
                .get(SavedSearchDetailsViewModule::class.java)
                .apply {
                    torrentListLiveData.observe(torrentListActivity, Observer {
                        toolbarTitle.text = it!!.searchQuery
                        showList(it.lastSearchedItems)
                    })
                    errorToastLiveData.observe(torrentListActivity, Observer {
                        if (it!!) {
                            showErrorMessage()
                            savedSearchDetailsViewModel.toastIsViewed()
                        }
                    })
                    showSwipeRefresh.observe(torrentListActivity, Observer {
                        swipe_container_list.isRefreshing = it!!
                    })
                }
    }

    private fun showList(list: List<TorrentItem>) {
        (torrentItemList.adapter as TorrentItemsAdapter).items = list
    }

    private fun showErrorMessage() {
        Toast.makeText(applicationContext, "Не удалось обновить список", Toast.LENGTH_SHORT).show()
    }

    private fun setupClickListener() {
        swipe_container_list.setOnRefreshListener { savedSearchDetailsViewModel.updateSearch(id) }
    }

    private fun setupRecyclerView() {
        torrentItemList.apply {
            adapter = TorrentItemsAdapter { uri -> downloadTorrent(uri) }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun downloadTorrent(torrentUri: String) {
        val uri = Uri.parse("http://nnm-club.name/forum/" + torrentUri)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}