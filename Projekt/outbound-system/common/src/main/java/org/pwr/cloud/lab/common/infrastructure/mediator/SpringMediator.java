package org.pwr.cloud.lab.common.infrastructure.mediator;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.common.application.cqs.*;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringMediator implements Mediator {

    private final ApplicationContext applicationContext;

    @Override
    public <R, C extends Command<R>> R send(C command) {
        if (command == null) {
            throw new NullPointerException("Command is null");
        }

        CommandHandler<C, R> handler = getHandler(CommandHandler.class, Command.class, command.getClass());
        return handler.handle(command);
    }

    @Override
    public <R, Q extends Query<R>> R ask(Q query) {
        if (query == null) {
            throw new NullPointerException("Query is null");
        }

        QueryHandler<Q, R> handler = getHandler(QueryHandler.class, Query.class, query.getClass());
        return handler.handle(query);
    }

    @SuppressWarnings("unchecked")
    private <H> H getHandler(Class<?> handlerInterface, Class<?> requestInterface, Class<?> requestClass) {
        var requestType = ResolvableType.forType(requestClass);
        var returnType = requestType.as(requestInterface).getGeneric(0);
        var handlerType = ResolvableType.forClassWithGenerics(handlerInterface, requestType, returnType);

        var beanNames = applicationContext.getBeanNamesForType(handlerType);
        if (beanNames.length == 0) {
            throw new IllegalStateException("No handler registered for: " + requestClass.getSimpleName());
        }

        return (H) applicationContext.getBean(beanNames[0]);
    }
}
