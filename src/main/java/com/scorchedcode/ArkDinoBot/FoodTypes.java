package com.scorchedcode.ArkDinoBot;

public enum FoodTypes {
    RAW_FISH_MEAT("Raw Fish Meat"),
    COOKED_FISH_MEAT("Cooked Fish Meat"),
    RAW_MEAT("Raw Meat"),
    COOKED_MEAT("Cooked Meat"),
    SPOILED_MEAT("Spoiled Meat"),
    MEJOBERRY("Mejoberry"),
    BERRY("Berry"),
    VEGETABLES("Vegetables"),
    RARE_FLOWER("Rare Flower"),
    CHITIN("Chitin"),
    KIBBLE("Kibble");

    private String friendly;
    FoodTypes(String friendly) {
        this.friendly = friendly;
    }

    String getFriendlyName() {
        return friendly;
    }

    static FoodTypes getFromFriendly(String name) {
        for(FoodTypes types : values()) {
            if(types.getFriendlyName().replaceAll(" ", "_").equalsIgnoreCase(name))
                return types;
        }
        return null;
    }
}
