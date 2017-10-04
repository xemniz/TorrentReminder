package ru.xmn.torrentreminder.screens.torrentlist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.di.TorrentModule
import ru.xmn.torrentreminder.features.torrent.domain.usecases.SavedSearchDetailsUseCase
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import javax.inject.Inject

class SavedSearchDetailsViewModule(val id: String) : ViewModel() {

    @Inject
    lateinit var savedSearchDetailsUseCase: SavedSearchDetailsUseCase
    val torrentListLiveData = MutableLiveData<TorrentSearch>()
    val errorToastLiveData = MutableLiveData<Boolean>()
    val showSwipeRefresh = MutableLiveData<Boolean>()

    init {
        App.component.torrentItemsComponent()
                .provideModule(TorrentModule())
                .build().inject(this)

        subscribeTorrentList(id)
    }


    fun subscribeTorrentList(id: String) {
        savedSearchDetailsUseCase.getTorrentList(id).subscribe { torrentListLiveData.value = it }
    }

    fun updateSearch(id: String) {
        savedSearchDetailsUseCase.updateSearch(id)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showSwipeRefresh.value = true }
                .onErrorComplete { errorToastLiveData.value = true; true }
                .subscribe { showSwipeRefresh.value = false }
    }

    fun toastIsViewed() {
        errorToastLiveData.value = false
    }

    class TorrentListViewModelFactory(val id: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SavedSearchDetailsViewModule(id) as T
        }
    }

}