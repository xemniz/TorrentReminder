package ru.xmn.torrentreminder.screens.torrentsearch.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.functions.BiFunction

import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.di.TorrentModule
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.usecases.SearchFragmentUseCase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchFragmentViewModel : ViewModel() {

    @Inject
    lateinit var searchFragmentUseCase: SearchFragmentUseCase
    val torrentListLiveData = MutableLiveData<SearchState>()
    val saveButtonShow = MutableLiveData<Boolean>()
    val searchQueryLiveData = MutableLiveData<String>()
    val searchQuerySubject: BehaviorProcessor<String> = BehaviorProcessor.create()

    init {
        App.component.torrentItemsComponent()
                .provideModule(TorrentModule())
                .build().inject(this)

        searchQuerySubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .switchMap { searchFlowable(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(SearchState.StartNewSearch)
                .subscribe { torrentListLiveData.value = it }

        Flowable.combineLatest<String, List<TorrentSearch>, Pair<String, List<TorrentSearch>>>(
                searchQuerySubject,
                searchFragmentUseCase.subscribeAllSearches(),
                BiFunction { query: String, searches: List<TorrentSearch> ->
                    Pair(query, searches)
                })
                .map {
                    val (query, searches) = it
                    query.length > 2 && !searches.any { it.searchQuery == query }
                }
                .subscribe {
                    saveButtonShow.value = it
                }

        searchQueryLiveData.value = searchQuerySubject.value ?: ""
    }

    fun addNewItem(searchQuery: String) {
        searchFragmentUseCase.addNewItem(searchQuery)
    }

    fun searchTorrents(query: String) {
        searchQuerySubject.onNext(query)
    }

    private fun searchFlowable(query: String) = when {
        query.length > 2 -> searchFragmentUseCase.search(query)
                .map {
                    when {
                        it.isEmpty() -> SearchState.Empty
                        else -> SearchState.Success(it)
                    }
                }
                .onErrorReturn { SearchState.Error(it) }
                .startWith(SearchState.Loading)
        else -> Flowable.just(SearchState.StartNewSearch)
    }

}


sealed class SearchState {
    class Success(val list: List<TorrentData>) : SearchState()
    class Error(val error: Throwable) : SearchState()
    object Loading : SearchState()
    object StartNewSearch : SearchState()
    object Empty : SearchState()
}
