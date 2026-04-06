package org.pwr.cloud.lab.common.event;

public record OutboundOrderCreatedEvent(String orderId, String fileName) implements DomainEvent {}
