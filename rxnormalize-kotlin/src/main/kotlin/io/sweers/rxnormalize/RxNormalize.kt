package io.sweers.rxnormalize

import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Adds a normalize() operator to `Observable`
 */
public inline fun <T> Observable<T>.normalize(window: Long, unit: TimeUnit): Observable<T> = normalize(window, unit, Schedulers.computation())

/**
 * Adds a normalize() operator to `Observable`
 */
public inline fun <T> Observable<T>.normalize(window: Long, unit: TimeUnit, scheduler: Scheduler): Observable<T> = lift(OperatorNormalize<T>(window, unit, scheduler))
