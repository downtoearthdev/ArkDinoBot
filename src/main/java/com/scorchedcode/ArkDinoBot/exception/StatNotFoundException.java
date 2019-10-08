package com.scorchedcode.ArkDinoBot.exception;

public class StatNotFoundException extends ReasonableException {
    @Override
    public String getReason() {
        return "This is not a valid stat!";
    }
}
