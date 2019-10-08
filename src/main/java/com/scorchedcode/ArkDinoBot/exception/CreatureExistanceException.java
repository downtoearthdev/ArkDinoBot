package com.scorchedcode.ArkDinoBot.exception;

public class CreatureExistanceException extends ReasonableException {
    private final String reason = "This creature is already registered.";
    private final String reasonTwo = "This creature is not registered.";
    private boolean isAdding;
    public CreatureExistanceException(boolean isAdding) {
        this.isAdding = isAdding;
    }
    @Override
    public String getReason() {
        return (isAdding ? reason : reasonTwo);
    }
}
