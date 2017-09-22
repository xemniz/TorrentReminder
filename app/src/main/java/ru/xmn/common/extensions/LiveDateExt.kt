package ru.xmn.common.extensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread

@MainThread
fun <X, Y> LiveData<X>.map(func: (X) -> Y): LiveData<Y> {
    val result = MediatorLiveData<Y>()
    result.addSource(this) { x -> result.setValue(func(x!!)) }
    return result
}

@MainThread
fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>): LiveData<Y> {
    val result = MediatorLiveData<Y>()
    result.addSource(this, object : Observer<X> {
        internal var mSource: LiveData<Y>? = null

        override fun onChanged(x: X?) {
            val newLiveData = func(x!!)
            if (mSource === newLiveData) {
                return
            }
            if (mSource != null) {
                result.removeSource(mSource)
            }
            mSource = newLiveData
            if (mSource != null) {
                result.addSource(mSource) { y -> result.setValue(y) }
            }
        }
    })
    return result
}