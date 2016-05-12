package io.sweers.rxnormalize

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import rx.schedulers.TestScheduler
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class OperatorNormalizeTest {

    @Test
    fun basic() {
        val scheduler = TestScheduler()
        val subject = PublishSubject.create<Int>()
        val o = RecordingObserver<Int>()
        subject.normalize(1, TimeUnit.SECONDS, scheduler).subscribe(o)

        subject.onNext(0)
        o.takeNext()

        subject.onNext(1)
        o.assertNoMoreEvents()

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        o.takeNext()

        subject.onCompleted()
        o.assertOnCompleted()
    }

    @Test
    fun buffer() {
        val scheduler = TestScheduler()
        val subject = PublishSubject.create<Int>()
        val o = RecordingObserver<Int>()
        subject.normalize(1, TimeUnit.SECONDS, scheduler).subscribe(o)

        // First emits immediately
        subject.onNext(0)
        o.takeNext()

        subject.onNext(1)
        subject.onNext(2)
        subject.onNext(3)
        o.assertNoMoreEvents()

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        o.takeNext()
        o.assertNoMoreEvents()
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        o.takeNext()
        o.assertNoMoreEvents()
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        o.takeNext()
        o.assertNoMoreEvents()

        subject.onCompleted()
        o.assertOnCompleted()
    }

    @Test
    fun completion() {
        val scheduler = TestScheduler()
        val subject = PublishSubject.create<Int>()
        val o = RecordingObserver<Int>()
        subject.normalize(1, TimeUnit.SECONDS, scheduler).subscribe(o)

        // First emits immediately
        subject.onNext(0)
        o.takeNext()

        subject.onNext(1)
        subject.onCompleted()
        o.assertNoMoreEvents()

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        o.takeNext()
        o.assertOnCompleted()
    }

    @Test
    fun error() {
        val scheduler = TestScheduler()
        val subject = PublishSubject.create<Int>()
        val o = RecordingObserver<Int>()
        subject.normalize(1, TimeUnit.SECONDS, scheduler).subscribe(o)

        // First emits immediately
        subject.onNext(0)
        o.takeNext()

        subject.onNext(1)
        subject.onError(RuntimeException("Blah"))
        o.assertNoMoreEvents()

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        o.takeNext()
        assertThat(o.takeError()).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun unsubscription() {
        val scheduler = TestScheduler()
        val subject = PublishSubject.create<Int>()
        val o = RecordingObserver<Int>()
        val sub = subject.normalize(1, TimeUnit.SECONDS, scheduler).subscribe(o)

        // First emits immediately
        subject.onNext(0)
        o.takeNext()

        subject.onNext(1)
        o.assertNoMoreEvents()

        sub.unsubscribe()

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        o.assertNoMoreEvents()
    }

    @Test
    fun overDelay_shouldEmitImmediately() {
        val scheduler = TestScheduler()
        val subject = PublishSubject.create<Int>()
        val o = RecordingObserver<Int>()
        subject.normalize(1, TimeUnit.SECONDS, scheduler).subscribe(o)

        // First emits immediately
        subject.onNext(0)
        o.takeNext()

        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        subject.onNext(1)
        o.takeNext()
    }

    @Test
    fun overDelay_withMultiple_emitsFirstAndStartsDrain() {
        val scheduler = TestScheduler()
        val subject = PublishSubject.create<Int>()
        val o = RecordingObserver<Int>()
        subject.normalize(1, TimeUnit.SECONDS, scheduler).subscribe(o)

        // First emits immediately
        subject.onNext(0)
        o.takeNext()

        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        subject.onNext(1)
        subject.onNext(2)
        subject.onNext(3)

        // Only one emitted
        o.takeNext()
        o.assertNoMoreEvents()

        // Drain the rest
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        o.takeNext()
        o.assertNoMoreEvents()

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        o.takeNext()
        o.assertNoMoreEvents()
    }
}

