package ru.xmn.common.rx

import io.reactivex.FlowableOperator
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
class CachePrevious<T>(val default: T) : FlowableOperator<Pair<T, T>, T> {
    override fun apply(observer: Subscriber<in Pair<T, T>>): Subscriber<in T> {
        return object : Subscriber<T> {
            private var previous: T = default

            override fun onNext(t: T?) {
                observer.onNext(Pair(previous, t ?: previous))
                t?.let { previous = t }
            }

            override fun onSubscribe(s: Subscription?) {
                observer.onSubscribe(s)
            }

            override fun onComplete() {
                observer.onComplete();
            }

            override fun onError(t: Throwable?) {
                observer.onError(t)
            }
        }
    }
}