package io.sweers.rxnormalize;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.Transformer;
import rx.schedulers.Schedulers;

public final class RxNormalize {
  private RxNormalize() {
    throw new AssertionError("No instances.");
  }

  public static <T> Transformer<T, T> transform(final long time, final TimeUnit unit) {
    return new Transformer<T, T>() {
      @Override
      public Observable<T> call(Observable<T> source) {
        return source
            .lift(new OperatorNormalize<T>(time, unit, Schedulers.computation()));
      }
    };
  }
}
