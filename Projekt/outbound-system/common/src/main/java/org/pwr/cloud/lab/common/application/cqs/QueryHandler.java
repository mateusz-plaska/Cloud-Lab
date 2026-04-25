package org.pwr.cloud.lab.common.application.cqs;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
