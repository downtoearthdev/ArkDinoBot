package com.scorchedcode.ArkDinoBot.exception;

public class CreatureNotFoundException extends ReasonableException {
    @Override
    public String getReason() {
        return "This is not a valid creature!";
    }
}
