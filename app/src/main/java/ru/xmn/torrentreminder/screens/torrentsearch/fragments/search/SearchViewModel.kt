package ru.xmn.torrentreminder.screens.torrentsearch.fragments.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.FlowableOperator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.processors.BehaviorProcessor
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import ru.xmn.common.rx.CachePrevious
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentItem
import ru.xmn.torrentreminder.features.torrent.domain.usecases.SearchResult
import ru.xmn.torrentreminder.features.torrent.domain.usecases.SearchUseCase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchFragmentViewModel : ViewModel() {

    @Inject
    lateinit var searchUseCase: SearchUseCase
    val torrentListLiveData = MutableLiveData<SearchState>()
    val saveButtonShow = MutableLiveData<Boolean>()
    val searchQueryLiveData = MutableLiveData<String>()
    private val searchQuerySubject: BehaviorProcessor<String> = BehaviorProcessor.createDefault("")

    init {
        App.component.torrentItemsComponent().build().inject(this)

        searchQuerySubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .switchMap { searchFlowable(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(SearchState.StartNewSearch)
                .subscribe { torrentListLiveData.value = it }

        searchUseCase.bindQueryFlowable(searchQuerySubject)

        Flowable.combineLatest<Boolean, Boolean, Boolean>(
                searchQuerySubject.map { queryIsNotTooSmall(it) },
                searchUseCase.searchIsSaved,
                BiFunction { queryIsNotTooSmall: Boolean, searchIsSaved: Boolean ->
                    queryIsNotTooSmall && !searchIsSaved
                })
                .subscribe { saveButtonShow.value = it }
    }

    fun addNewItem(searchQuery: String) {
        searchUseCase.addNewItem(searchQuery)
    }

    fun searchTorrents(query: String) {
        searchQuerySubject.onNext(query)
    }

    private fun searchFlowable(query: String) = when {
        queryIsNotTooSmall(query) ->
            searchUseCase.searchResult
                    .map {
                        when (it) {
                            is SearchResult.NewSearch -> SearchState.Success(it.list, true)
                            is SearchResult.SavedSearch -> SearchState.Success(it.search.lastSearchedItems, false)
                        } as SearchState
                    }
                    .onErrorReturn { SearchState.Error(it) }
                    .startWith(SearchState.Loading)
        else ->
            Flowable.just(SearchState.StartNewSearch)
    }

    private fun queryIsNotTooSmall(query: String) = query.length > 2
}


sealed class SearchState {
    class Success(val list: List<TorrentItem>, val newSearch: Boolean) : SearchState()
    class Error(val error: Throwable) : SearchState(){init {
        error.printStackTrace()
    }}
    object Loading : SearchState()
    object StartNewSearch : SearchState()
}
