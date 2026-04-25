package org.pwr.cloud.lab.common.application.cqs;

public interface Mediator {
    <R, C extends Command<R>> R send(C command);

    <R, Q extends Query<R>> R ask(Q query);
}
