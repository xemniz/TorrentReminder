package ru.xmn.torrentreminder.screens.torrentsearch

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.TorrentSearchUseCase
import javax.inject.Inject

class TorrentSearchViewModel : ViewModel() {

    @Inject
    lateinit var torrentSearchUseCase: TorrentSearchUseCase
    val torrentItemsLiveData = MutableLiveData<TorrentSearchState>()

    init {
        App.component.torrentItemsComponent().provideModule(TorrentSearchModule()).build().inject(this)
        torrentSearchUseCase.subscribeAllSearches()
                .map { TorrentSearchState.Success(it) as TorrentSearchState }
                .startWith(TorrentSearchState.Loading)
                .onErrorReturn { TorrentSearchState.Error(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { torrentItemsLiveData.value = it }
        updateAllItems()
    }

    fun startCreateNewSearch() {
        torrentSearchUseCase.addEmptyItem()
    }

    fun updateSearch(id:String, query: String) {
        torrentSearchUseCase.search(id, query)
                .doOnError { TorrentSearchState.Error(it); torrentSearchUseCase.addEmptyItem() }
                .subscribe { torrentSearchUseCase.torrentSearchRepository.update(id, query, it) }
    }

    fun updateAllItems(){
        torrentSearchUseCase.updateItems()
                .subscribe {
                    for (item in it) {
                        torrentSearchUseCase.search(item.id, item.searchQuery)
                    }
                    TorrentSearchState.UpdateComplete(true)
                }
    }

    fun deleteItem(query: String){
        torrentSearchUseCase.delete(query)
    }
}