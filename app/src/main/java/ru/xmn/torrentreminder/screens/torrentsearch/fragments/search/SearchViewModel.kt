package ru.xmn.torrentreminder.screens.torrentsearch.fragments.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.domain.TorrentItem
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
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
    private var subscription: Disposable? = null

    init {
        App.component.torrentItemsComponent().build().inject(this)

        torrentListLiveData.value = SearchState.StartNewSearch

        searchQuerySubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { searchFlowable(it) }

        Flowable.combineLatest<Boolean, Boolean, Boolean>(
                searchQuerySubject.map { queryIsNotTooSmall(it) },
                searchUseCase.isSearchSaved(searchQuerySubject),
                BiFunction { queryIsNotTooSmall: Boolean, searchIsSaved: Boolean ->
                    queryIsNotTooSmall && !searchIsSaved
                })
                .distinctUntilChanged()
                .subscribe { saveButtonShow.value = it }
    }

    fun addNewItem(searchQuery: String) {
        searchUseCase.addNewItem(searchQuery)
                .flatMap { searchQuerySubject }
                .filter { it == searchQuery }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ searchFlowable(it) }, { it.printStackTrace() })
    }

    fun markAsViewed(id: String) {
        Observable.just(true).delay(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable { Completable.fromCallable { searchUseCase.markAsViewed(id) } }
                .subscribe()
    }

    fun searchTorrents(query: String) {
        searchQuerySubject.onNext(query)
    }

    private fun searchFlowable(query: String) {
        subscription?.apply {
            if (!subscription!!.isDisposed)
                subscription!!.dispose()
        }

        subscription = when {
            queryIsNotTooSmall(query) ->
                searchUseCase.foundedOrSavedItems(query)
                        .map {
                            when (it) {
                                is SearchResult.NewSearch -> SearchState.SuccessNewSearch(it.list)
                                is SearchResult.SavedSearch -> SearchState.SuccessSavedSearch(it.search)
                                is SearchResult.Error -> SearchState.Error(it.error)
                            }
                        }
                        .onErrorReturn { SearchState.Error(it) }
                        .startWith(SearchState.Loading)
            else ->
                Flowable.just(SearchState.StartNewSearch)
        }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { torrentListLiveData.value = it }
    }

    private fun queryIsNotTooSmall(query: String) = query.length > 2
}


sealed class SearchState {
    class SuccessNewSearch(val list: List<TorrentItem>) : SearchState()
    class SuccessSavedSearch(val search: TorrentSearch) : SearchState()
    class Error(val error: Throwable) : SearchState() {init {
        error.printStackTrace()
    }
    }

    object Loading : SearchState()
    object StartNewSearch : SearchState()
}
