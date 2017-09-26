package ru.xmn.torrentreminder.screens.torrentsearch

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.TorrentSearchUseCase
import ru.xmn.torrentreminder.features.torrent.UpdateItemsResult
import javax.inject.Inject

class TorrentSearchViewModel : ViewModel() {

    @Inject
    lateinit var torrentSearchUseCase: TorrentSearchUseCase
    val torrentItemsLiveData = MutableLiveData<List<TorrentSearch>>()
    val errorToastLiveData = MutableLiveData<ToastMsg>()
    val showSwipeRefresh = MutableLiveData<Boolean>()

    init {
        App.component.torrentItemsComponent().provideModule(TorrentSearchModule()).build().inject(this)
        torrentSearchUseCase.subscribeAllSearches()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { torrentItemsLiveData.value = it }
        updateAllItems()
    }

    fun createNewSearch() {
        torrentSearchUseCase.addEmptyItem()
    }

    fun updateSearch(id: String, query: String) {
        torrentSearchUseCase.firstSearchOnItem(id, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete { errorToastLiveData.value = ToastMsg.UPDATING_ERROR; true }
                .subscribe()
    }

    fun updateAllItems() {
        torrentSearchUseCase
                .updateItems()
                .doOnSubscribe { showSwipeRefresh.value = true }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    showSwipeRefresh.value = false
                    when (result) {
                        UpdateItemsResult.SUCCESS -> {
                        }
                        UpdateItemsResult.ERROR -> {
                            errorToastLiveData.value = ToastMsg.UPDATING_ERROR
                            showSwipeRefresh.value = false
                        }
                    }
                }
    }

    fun deleteItem(query: String) {
        torrentSearchUseCase.delete(query)
    }

    fun toastIsViewed() {
        errorToastLiveData.value = ToastMsg.NOTHING
    }
}

enum class ToastMsg {
    NOTHING, UPDATING_ERROR
}
