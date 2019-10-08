package com.scorchedcode.ArkDinoBot;

public enum DinoStats {
    HEALTH,
    STAMINA,
    OXYGEN,
    FOOD,
    WEIGHT,
    BASE_DAMAGE,
    MOVEMENT_SPEED,
    TORPOR;

    public String getFriendlyName() {
        return this.name().replaceAll("_", " ");
    }
}
