package com.scorchedcode.ArkDinoBot;

import com.scorchedcode.ArkDinoBot.exception.CreatureNotFoundException;
import com.scorchedcode.ArkDinoBot.exception.StatNotFoundException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static String getChromedriverName() {
        String OS = System.getProperty("os.name");
        return (OS.contains("win") ? "chromedriver.exe" : "chromedriver");
    }

    public static String getDinoAndAmount(String input) {
        Matcher match = Pattern.compile("\\d \\w+\\.").matcher(input);
        match.find();
        return match.group().replaceAll("\\.", "");
    }

    public static CreatureTypes acceptableName(String name) throws CreatureNotFoundException
    {
        for(CreatureTypes value : CreatureTypes.values()) {
            if(value.toString().replaceAll("_", " ").toLowerCase().contains(name.toLowerCase()))
                return value;
        }
        throw new CreatureNotFoundException();
    }

    public static  DinoStats acceptableStat(String name) throws StatNotFoundException {
        for(DinoStats value : DinoStats.values()) {
            if(value.toString().replaceAll("_", " ").toLowerCase().contains(name.toLowerCase().replaceAll("_", " ")))
                return value;
        }
        throw new StatNotFoundException();
    }

}
