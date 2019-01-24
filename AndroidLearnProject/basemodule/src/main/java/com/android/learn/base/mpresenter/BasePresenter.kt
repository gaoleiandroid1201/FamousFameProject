package com.android.learn.base.mpresenter

import com.android.learn.base.thirdframe.retrofit.ApiService
import com.android.learn.base.thirdframe.retrofit.RetrofitProvider
import com.android.learn.base.thirdframe.rxjava.BaseObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

abstract class BasePresenter<V> {

    var mView: V? = null
    //    public Observable observable;
    var mRestService = RetrofitProvider.getInstance().builder().apiService
    internal var mCompositeDisposable: CompositeDisposable? = null

    /**
     * 绑定View
     *
     * @param view
     */
    fun attach(view: V) {
        this.mView = view
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
    }

    /**
     * 释放View
     */
    fun dettach() {
        this.mView = null
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
    }

    fun addSubscribe(observable: Observable<*>,  observer: BaseObserver<*>) {
        mCompositeDisposable!!.add(observer)
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }
}