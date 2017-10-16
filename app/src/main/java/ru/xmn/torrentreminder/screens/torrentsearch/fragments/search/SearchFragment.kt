package ru.xmn.torrentreminder.screens.torrentsearch.fragments.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.transition.*
import android.support.transition.TransitionSet.ORDERING_TOGETHER
import android.support.v7.widget.DividerItemDecoration
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.SearchView
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.android.synthetic.main.fragment_torrent_search.*
import ru.xmn.common.extensions.hideKeyboard
import ru.xmn.common.extensions.log
import ru.xmn.common.extensions.visibleOnly
import ru.xmn.torrentreminder.R
import kotlin.properties.Delegates


class SearchFragment : android.support.v4.app.Fragment() {

    lateinit var searchFragmentViewModel: SearchFragmentViewModel
    var initialQuery: String by Delegates.observable("") { _, _, query ->
        torrent_search_view.setQuery(query, true)
        log("initialQuery: String by Delegates.observable; torrent_search_view = $torrent_search_view")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater!!.inflate(R.layout.fragment_torrent_search, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareSearchView()
        setupViewModel()
        setupListeners()
        setupRecyclerView()
    }

    private fun prepareSearchView() {
        val searchPlateId = torrent_search_view.getContext().getResources().getIdentifier("android:id/search_plate", null, null)
        val searchPlate = torrent_search_view.findViewById<LinearLayout>(searchPlateId)
        searchPlate.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setupViewModel() {
        searchFragmentViewModel = ViewModelProviders
                .of(this)
                .get(SearchFragmentViewModel::class.java)
                .apply {
                    torrentListLiveData.observe(this@SearchFragment, Observer {
                        showState(it!!)
                    })
                    saveButtonShow.observe(this@SearchFragment, Observer {
                        updateFab(it!!)
                    })
                    torrent_search_view.setQuery(searchQueryLiveData.value, false)
                }
    }

    private fun setupListeners() {
        torrent_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                searchFragmentViewModel.pushQuery(p0)
                return true
            }

            override fun onQueryTextChange(p0: String): Boolean {
                searchFragmentViewModel.pushQuery(p0)
                return true
            }

        })
        fab.setOnClickListener {
            searchFragmentViewModel.addNewItem(torrent_search_view.query.toString())
        }
    }

    private fun showState(state: SearchState) {
        val layouts = listOf<View>(error_layout, empty_search_layout, start_search_layout, torrent_searched_list, progress)
        println(state)
        TransitionManager.beginDelayedTransition(container, getTransition())
        when (state) {
            is SearchState.StartNewSearch -> {
                layouts.visibleOnly(start_search_layout)
                (torrent_searched_list.adapter as TorrentItemsAdapter).items = emptyList()
            }
            is SearchState.Loading -> {
                layouts.visibleOnly(progress)
                (torrent_searched_list.adapter as TorrentItemsAdapter).items = emptyList()
            }
            is SearchState.SuccessNewSearch -> {
                if (state.list.isNotEmpty()) {
                    layouts.visibleOnly(torrent_searched_list)
                    (torrent_searched_list.adapter as TorrentItemsAdapter).items = state.list

                } else {
                    layouts.visibleOnly(empty_search_layout)
                    (torrent_searched_list.adapter as TorrentItemsAdapter).items = emptyList()
                    empty_search_layout.text = resources.getString(R.string.common_nothing_found_search_text)
                }
            }
            is SearchState.SuccessSavedSearch -> {
                if (state.search.lastSearchedItems.isNotEmpty()) {
                    layouts.visibleOnly(torrent_searched_list)
                    (torrent_searched_list.adapter as TorrentItemsAdapter).items = state.search.lastSearchedItems
                    searchFragmentViewModel.markAsViewed(state.search.id)
                } else {
                    layouts.visibleOnly(empty_search_layout)
                    (torrent_searched_list.adapter as TorrentItemsAdapter).items = emptyList()
                    empty_search_layout.text = resources.getString(R.string.nothing_found_saved_search_text)
                }
            }
            is SearchState.Error -> {
                layouts.visibleOnly(error_layout)
                error_button.setOnClickListener { searchFragmentViewModel.pushQuery(torrent_search_view.query.toString()) }
                (torrent_searched_list.adapter as TorrentItemsAdapter).items = emptyList()
            }
        }
    }

    private fun getTransition(): Transition {
        val listTransitionFade = Fade().apply { duration = 200 }.addTarget(torrent_searched_list)

        val listTransitionSlide = Slide().apply {
            duration = 200
            slideEdge = Gravity.BOTTOM

        }.addTarget(torrent_searched_list)

        val elementsTransitionSlide = Slide().apply {
            slideEdge = Gravity.TOP
            duration = 300
            addTarget(error_layout)
            addTarget(empty_search_layout)
            addTarget(start_search_layout)
        }

        val elementsTransitionFade = Fade().apply {
            duration = 300
            addTarget(error_layout)
            addTarget(empty_search_layout)
            addTarget(start_search_layout)
            addTarget(progress)
        }

        return TransitionSet().apply {
            addTransition(elementsTransitionSlide)
            addTransition(elementsTransitionFade)
            addTransition(listTransitionSlide)
            addTransition(listTransitionFade)
            ordering = ORDERING_TOGETHER
        }
    }

    private fun setupRecyclerView() {
        torrent_searched_list.apply {
            adapter = TorrentItemsAdapter { uri -> downloadTorrent(uri) }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            setOnTouchListener { _, _ -> torrent_searched_list.hideKeyboard(); false }
            itemAnimator = FadeInUpAnimator().apply { supportsChangeAnimations = true }
        }
    }

    private fun downloadTorrent(torrentUri: String) {
        val uri = Uri.parse("http://nnm-club.name/forum/" + torrentUri)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }


    private fun updateFab(show: Boolean) {
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