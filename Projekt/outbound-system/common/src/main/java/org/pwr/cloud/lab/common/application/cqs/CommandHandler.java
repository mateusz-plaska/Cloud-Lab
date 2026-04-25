package org.pwr.cloud.lab.common.application.cqs;

public interface CommandHandler<C extends Command<R>, R> {
    R handle(C command);
}
