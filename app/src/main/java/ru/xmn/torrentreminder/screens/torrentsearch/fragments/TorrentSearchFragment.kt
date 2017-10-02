package ru.xmn.torrentreminder.screens.torrentsearch.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_torrent_search.*
import kotlinx.android.synthetic.main.fragment_torrent_search.view.*
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.screens.torrentsearch.adapters.SearchFragmentAdapter

/**
 * Created by Michael on 02.10.2017.
 *
 */

class TorrentSearchFragment: android.support.v4.app.Fragment(){

    lateinit var searchFragmentViewModel: SearchFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_torrent_search, container, false)
        setupViewModel()
        setupClickListener(view)
        setupRecyclerView(view)

        return view
    }

    private fun setupViewModel() {
        val torrentSearchActivity = this
        searchFragmentViewModel = ViewModelProviders
                .of(torrentSearchActivity)
                .get(SearchFragmentViewModel::class.java)
                .apply {
                    torrentListLiveData.observe(torrentSearchActivity, Observer {
                        Log.d("My", it!!.size.toString())
                        showList(it)

                    })
                    errorToastLiveData.observe(torrentSearchActivity, Observer {
                        showErrorMessage()
                        searchFragmentViewModel.toastIsViewed()
                    })
                }
    }

    private fun showErrorMessage() {
        Toast.makeText(context, "Не удалось выполнить поиск", Toast.LENGTH_SHORT).show()
    }

    private fun setupClickListener(view: View){
        view.add_torrent.setOnClickListener {searchFragmentViewModel.addNewItem(view.search_torrent.text.toString()) }

        view.search_torrent.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0 != null && p0.length > 2) searchFragmentViewModel.searchTorrents(p0.toString())
            }
        })
    }

    private fun showList(list: List<TorrentData>){
        (torrentSearchedList.adapter as SearchFragmentAdapter).items = list
    }

    private fun setupRecyclerView(view: View) {
        view.torrentSearchedList.apply {
            adapter = SearchFragmentAdapter { uri -> downloadTorrent(uri) }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun downloadTorrent(torrentUri: String) {
        val uri = Uri.parse("http://live-rutor.org" + torrentUri)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}