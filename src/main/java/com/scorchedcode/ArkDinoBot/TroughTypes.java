package com.scorchedcode.ArkDinoBot;

public enum TroughTypes {
    NORMAL("Normal"),
    TEK("Tek"),
    CLICKER("Clicker");

    private String friendly;
    TroughTypes(String friendly) {
        this.friendly = friendly;
    }

    String getFriendlyName() {
        return friendly;
    }

    static TroughTypes getFromFriendly(String name) {
        for(TroughTypes types : values()) {
            if(types.getFriendlyName().replaceAll(" ", "_").equalsIgnoreCase(name))
                return types;
        }
        return null;
    }
}
