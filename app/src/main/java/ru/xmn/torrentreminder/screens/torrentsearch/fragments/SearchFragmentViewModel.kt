package ru.xmn.torrentreminder.screens.torrentsearch.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.di.TorrentModule
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.usecases.SearchFragmentUseCase
import javax.inject.Inject

/**
 * Created by Michael on 02.10.2017.
 *
 */
class SearchFragmentViewModel: ViewModel() {

    @Inject
    lateinit var searchFragmentUseCase: SearchFragmentUseCase
    val torrentListLiveData = MutableLiveData<List<TorrentData>>()
    val errorToastLiveData = MutableLiveData<Boolean>()

    init {
        App.component.torrentItemsComponent()
                .provideModule(TorrentModule())
                .build().inject(this)
    }

    fun addNewItem(searchQuery: String){
        searchFragmentUseCase.addNewItem(searchQuery)
    }

    fun searchTorrents(query: String){
        searchFragmentUseCase.firstSearchOnItem(query)
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { errorToastLiveData.value = true }
                .subscribe {
                    Log.d("My", it[0].name)
                    torrentListLiveData.value = it
                    errorToastLiveData.value = false
                }
    }

    fun toastIsViewed() {
        errorToastLiveData.value = false
    }
}