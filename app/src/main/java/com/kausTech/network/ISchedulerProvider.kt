package com.kausTech.network

import io.reactivex.Scheduler

interface ISchedulerProvider {
    fun ui(): Scheduler

    fun computation(): Scheduler

    fun io(): Scheduler
}
