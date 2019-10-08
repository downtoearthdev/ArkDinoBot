package com.scorchedcode.ArkDinoBot.exception;

public class MemberRegistrationException extends ReasonableException {

    private final String reason = "This member is already registered";
    private final String reasonTwo = "This member is not registered yet";
    private boolean isAdding;

    public MemberRegistrationException(boolean isAdding) {
        this.isAdding = isAdding;
    }
    @Override
    public String getReason() {
        return (isAdding ? reason : reasonTwo);
    }
}
