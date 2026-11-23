package com.sugarplumstudios.plushies.network;


/** A server -> client message. Handled on the client's main thread. */
public interface ClientboundMessage extends Message {

    /** Runs on the client only, so it may use client classes. */
    void handleOnClient();
}
