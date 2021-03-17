package com.wlj.sate7weather.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class NoStickLiveData<T> : LiveData<T?>() {
    private var isAllowNullValue = false
    private val observers: HashMap<Int, Boolean?> = HashMap()
    fun observeInActivity(activity: AppCompatActivity,observer: Observer<in T?>) {
        val owner: LifecycleOwner = activity
        val storeId =
            System.identityHashCode(observer) //源码这里是activity.getViewModelStore()，是为了保证同一个ViewModel环境下"唯一可信源"
        observe(storeId, owner, observer)
    }

    private fun observe(storeId: Int,owner: LifecycleOwner,observer: Observer<in T?>
    ) {
        if (observers[storeId] == null) {
            observers[storeId] = true
        }
        super.observe(owner, Observer { t: T? ->
            if (!observers[storeId]!!) {
                observers[storeId] = true
                if (t != null || isAllowNullValue) {
                    observer.onChanged(t)
                }
            }
        })
    }

    override fun setValue(value: T?) {
        if (value != null || isAllowNullValue) {
            for (entry in observers.entries) {
                entry.setValue(false)
            }
            super.setValue(value)
        }
    }

    override fun postValue(value: T?) {
        if (value != null || isAllowNullValue) {
            for (entry in observers.entries) {
            }
            super.setValue(value)
        }
    }

    protected fun clear() {
        super.setValue(null)
    }
}