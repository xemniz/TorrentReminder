package ru.xmn.torrentreminder.screens.torrentsearch.fragments.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.transition.AutoTransition
import android.support.transition.TransitionManager
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.SearchView
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.android.synthetic.main.fragment_torrent_search.*
import ru.xmn.common.extensions.hideKeyboard
import ru.xmn.common.extensions.visibleOnly
import ru.xmn.torrentreminder.R

class SearchFragment : android.support.v4.app.Fragment() {

    lateinit var searchFragmentViewModel: SearchFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater!!.inflate(R.layout.fragment_torrent_search, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupClickListener()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        searchFragmentViewModel = ViewModelProviders
                .of(this)
                .get(SearchFragmentViewModel::class.java)
                .apply {
                    torrentListLiveData.observe(this@SearchFragment, Observer {
                        showState(it!!)
                    })
                    torrent_search_view.setQuery(searchQueryLiveData.value, false)
                    saveButtonShow.observe(this@SearchFragment, Observer {
                        updateScreen(it!!)
                    })
                }
    }

    private fun setupClickListener() {
        torrent_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                searchFragmentViewModel.searchTorrents(p0)
                return true
            }

            override fun onQueryTextChange(p0: String): Boolean {
                searchFragmentViewModel.searchTorrents(p0)
                return true
            }

        })
        fab.setOnClickListener {
            searchFragmentViewModel.addNewItem(torrent_search_view.query.toString())
        }
    }

    private fun showState(state: SearchState) {
        val layouts = listOf<View>(error_layout, empty_search_layout, start_search_layout, torrent_searched_list, progress)
        TransitionManager.beginDelayedTransition(container, AutoTransition().apply { duration = 100 })
        when (state) {
            is SearchState.StartNewSearch -> {
                layouts.visibleOnly(start_search_layout)
                (torrent_searched_list.adapter as TorrentDataAdapter).items = emptyList()
            }
            is SearchState.Loading -> {
                layouts.visibleOnly(progress)
                (torrent_searched_list.adapter as TorrentDataAdapter).items = emptyList()
            }
            is SearchState.Empty -> {
                layouts.visibleOnly(empty_search_layout)
                (torrent_searched_list.adapter as TorrentDataAdapter).items = emptyList()
            }
            is SearchState.Success -> {
                layouts.visibleOnly(torrent_searched_list)
                (torrent_searched_list.adapter as TorrentDataAdapter).items = state.list
            }
            is SearchState.Error -> {
                layouts.visibleOnly(error_layout)
                error_button.setOnClickListener { searchFragmentViewModel.searchTorrents(torrent_search_view.query.toString()) }
                (torrent_searched_list.adapter as TorrentDataAdapter).items = emptyList()
            }
        }
    }

    private fun setupRecyclerView() {
        torrent_searched_list.apply {
            adapter = TorrentDataAdapter { uri -> downloadTorrent(uri) }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            setOnTouchListener { _, _ -> torrent_searched_list.hideKeyboard(); false }
            itemAnimator = FadeInUpAnimator()
        }
    }

    private fun downloadTorrent(torrentUri: String) {
        val uri = Uri.parse("http://nnm-club.name/forum/" + torrentUri)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }


    private fun updateScreen(showButtonSave: Pair<Boolean, Boolean>) {
        val (previous, show) = showButtonSave
        if (previous == show) return

        when {
            !show ->
                fab.animate().translationX(300f)
                        .apply { interpolator = AccelerateInterpolator() }
                        .start()

            show ->
                fab.animate().translationX(0f)
                        .apply { interpolator = DecelerateInterpolator() }
                        .start()
        }
    }
}