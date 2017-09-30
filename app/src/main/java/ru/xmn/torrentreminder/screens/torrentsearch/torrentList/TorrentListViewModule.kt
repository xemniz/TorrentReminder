package ru.xmn.torrentreminder.screens.torrentsearch.torrentList

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.TorrentSearch
import ru.xmn.torrentreminder.screens.torrentsearch.TorrentSearchModule
import javax.inject.Inject

/**
 * Created by Michael on 29.09.2017.
 *
 */
class TorrentListViewModule : ViewModel() {

    @Inject
    lateinit var torrentListUseCase: TorrentListUseCase
    val torrentListLiveData = MutableLiveData<TorrentSearch>()
    val errorToastLiveData = MutableLiveData<Boolean>()
    val showSwipeRefresh = MutableLiveData<Boolean>()

    init {
        App.component.torrentItemsComponent().provideModule(TorrentSearchModule()).build().injectToListVM(this)
    }


    fun getTorrentList(query: String) {
        torrentListUseCase.getTorrentList(query)?.subscribe { torrentListLiveData.value = it }
    }

    fun updateSearch(id: String, query: String) {
        torrentListUseCase.firstSearchOnItem(id, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete { errorToastLiveData.value = true; true }
                .subscribe{ showSwipeRefresh.value = false; errorToastLiveData.value = false }
    }

}