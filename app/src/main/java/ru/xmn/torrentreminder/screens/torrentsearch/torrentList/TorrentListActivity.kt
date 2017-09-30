package ru.xmn.torrentreminder.screens.torrentsearch.torrentList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.torrent_item_list.*
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.TorrentItem

/**
 * Created by Michael on 27.09.2017.
 *
 */
class TorrentListActivity : AppCompatActivity(){

    lateinit var torrentListViewModel: TorrentListViewModule
    lateinit var id: String
    lateinit var query: String

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent.extras
        query = intent.getString("query")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.torrent_item_list)
        setupViewModel()
        setupRecyclerView()
        setTorrentList(query)
        setupClickListener()
    }

    private fun setupViewModel(){
        val torrentListActivity = this
        torrentListViewModel = ViewModelProviders.of(this).get(TorrentListViewModule::class.java).apply {
            torrentListLiveData.observe(torrentListActivity, Observer {
                id = it?.id ?: ""
                val list = it?.lastSearchedItems ?: emptyList<TorrentItem>()
                showList(list)
            })
            errorToastLiveData.observe(torrentListActivity, Observer {
                if (it != null && it) showErrorMessage()
            })
            showSwipeRefresh.observe(torrentListActivity, Observer {
                swipe_container_list.isRefreshing = it ?: true
            })
        }
    }

    private fun setTorrentList(query: String){
        torrentListViewModel.getTorrentList(query)
    }

    private fun showList(list: List<TorrentItem>){
        (torrentItemList.adapter as ListAdapter).items = list
    }

    private fun showErrorMessage(){
        Toast.makeText(applicationContext, "Не удалось обновить список", Toast.LENGTH_SHORT).show()
    }

    private fun setupClickListener(){
        swipe_container_list.setOnRefreshListener { torrentListViewModel.updateSearch(id, query) }
    }

    private fun setupRecyclerView(){
        torrentItemList.adapter = ListAdapter{ uri -> downloadTorrent(uri) }
    }

    private fun downloadTorrent(torrentUri: String){
        val uri = Uri.parse("http://live-rutor.org" + torrentUri)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}