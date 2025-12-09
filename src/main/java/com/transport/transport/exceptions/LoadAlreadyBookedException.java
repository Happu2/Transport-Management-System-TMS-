package com.transport.transport.exceptions;

public class LoadAlreadyBookedException extends RuntimeException {
    public LoadAlreadyBookedException(String msg) {
        super(msg);
    }
}
