package ru.xmn.torrentreminder.screens.torrentitem

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Subcomponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.torrent_list.*
import ru.xmn.torrentreminder.R
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.application.di.scopes.ActivityScope
import ru.xmn.torrentreminder.features.torrent.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.TorrentSearchUseCase
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject
import javax.inject.Inject


class TorrentItemActivity : AppCompatActivity() {
    lateinit var torrentItemsViewModel: TorrentItemsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.torrent_list)
        setupToolbar()
        setupClickListeners()
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        torrentItemsViewModel = ViewModelProviders.of(this).get(TorrentItemsViewModel::class.java)
        torrentItemsViewModel.torrentItemsLiveData.observe(this, Observer {
            when (it) {
                is TorrentItemsState.Loading -> showLoading()
                is TorrentItemsState.Success -> showValue(it.items)
                is TorrentItemsState.Error -> showError()
            }
        })
    }

    private fun showError() {

    }

    private fun showValue(items: List<TorrentSearchViewItem>) {
        (torrentItemsList.adapter as TorrentItemAdapter).items = items
    }

    private fun showLoading() {
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupClickListeners() {
        fab.setOnClickListener { view ->
            torrentItemsViewModel.startCreateNewSearch()
        }
    }

    private fun setupRecyclerView() {
        torrentItemsList.adapter = TorrentItemAdapter()
    }

}

class TorrentItemsViewModel : ViewModel() {

    @Inject
    lateinit var torrentSearchUseCase: TorrentSearchUseCase
    val torrentItemsLiveData = MutableLiveData<TorrentItemsState>()

    init {
        App.component.torrentItemsComponent().provideModule(TorrentItemsRepository()).build().inject(this)
        torrentSearchUseCase.subscribeAllSearches()
                .map<List<TorrentSearchViewItem>> { it.map { TorrentSearchViewItem.Common(it) } }
                .map { listOf<TorrentSearchViewItem>(TorrentSearchViewItem.NewItem(), *it.toTypedArray()) }
                .map { TorrentItemsState.Success(it) as TorrentItemsState }
                .startWith(TorrentItemsState.Loading)
                .onErrorReturn { TorrentItemsState.Error(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { torrentItemsLiveData.value = it }
    }


    fun startCreateNewSearch() {
    }
}

sealed class TorrentItemsState {
    object Loading : TorrentItemsState()

    class Success(val items: List<TorrentSearchViewItem>) : TorrentItemsState()

    class Error(val error: Throwable) : TorrentItemsState()
}


@Module
class TorrentItemsRepository

@ActivityScope
@Subcomponent(modules = arrayOf(TorrentItemsRepository::class))
interface TorrentItemsComponent {
    fun inject(abstractViewModel: TorrentItemsViewModel)

    @Subcomponent.Builder
    interface Builder {
        fun build(): TorrentItemsComponent
        fun provideModule(r: TorrentItemsRepository): Builder

    }
}
