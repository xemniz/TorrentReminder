package ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator
import kotlinx.android.synthetic.main.fragment_torrent_track.*
import kotlinx.android.synthetic.main.fragment_torrent_track.view.*
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch

class SavedSearchesFragment : android.support.v4.app.Fragment() {

    private lateinit var trackFragmentViewModel: TorrentSearchViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_torrent_track, container, false)

        setupViewModel()
        setupClickListeners(view)
        setupRecyclerView(view)
        return view
    }

    private fun setupViewModel() {
        val torrentSearchActivity = this
        trackFragmentViewModel = ViewModelProviders.of(torrentSearchActivity).get(TorrentSearchViewModel::class.java).apply {
            torrentItemsLiveData.observe(torrentSearchActivity, Observer {
                showValue(it!!)
            })
            errorToastLiveData.observe(torrentSearchActivity, Observer {
                when (it) {
                    ToastMsg.NOTHING -> {
                    }
                    ToastMsg.UPDATING_ERROR -> {
                        showUpdatingError()
                        trackFragmentViewModel.toastIsViewed()
                    }
                }
            })
            showSwipeRefresh.observe(torrentSearchActivity, Observer {
                swipe_container.isRefreshing = it ?: false
            })
        }
    }

    private fun showUpdatingError() {
        Toast.makeText(context, "Не удалось обновить поиск", Toast.LENGTH_SHORT).show()
    }

    private fun showValue(items: List<TorrentSearch>) {
        (torrentItemsList.adapter as SavedSearchesAdapter).items = items
        updateScreen(items.any { it.searchQuery == "" })
    }

    private fun updateScreen(hasNewItem: Boolean) {
        val hided = fab.translationX > 0f

        val needToHide = hasNewItem && !hided
        val needToShow = !hasNewItem && hided

        when {
            needToHide ->
                fab.animate().translationX(300f)
                        .apply { interpolator = AccelerateInterpolator() }
                        .start()

            needToShow ->
                fab.animate().translationX(0f)
                        .apply { interpolator = DecelerateInterpolator() }
                        .start()
        }
    }


    private fun setupClickListeners(view: View) {
        view.fab.setOnClickListener {
            trackFragmentViewModel.createNewSearch()
        }
        view.swipe_container.setOnRefreshListener { trackFragmentViewModel.updateAllItems() }
    }

    private fun setupRecyclerView(view: View) {
        view.torrentItemsList.apply {
            layoutManager = object : LinearLayoutManager(view.context) {
                override fun onRequestChildFocus(parent: RecyclerView?, state: RecyclerView.State?, child: View?, focused: View?): Boolean {
                    return true
                }

                override fun onInterceptFocusSearch(focused: View?, direction: Int): View? {
                    return focused
                }
            }
            adapter = SavedSearchesAdapter(
                    torrentSearchStart = { id, query ->
                        trackFragmentViewModel.firstSearchOnItem(id, query)
                    },
                    deleteItem = {
                        trackFragmentViewModel.deleteItem(it)
                    },
                    onInsertedAction = { position, _ ->
                        torrentItemsList.smoothScrollToPosition(position)
                    })
            itemAnimator = FadeInDownAnimator()
        }
    }

    fun deleteNewSearch() {
        try {
            trackFragmentViewModel.deleteNewSearch()
        } catch (e: Throwable) {
        }
    }

}