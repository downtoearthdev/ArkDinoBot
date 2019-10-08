package com.scorchedcode.ArkDinoBot;

import com.scorchedcode.ArkDinoBot.exception.CreatureExistanceException;
import com.scorchedcode.ArkDinoBot.exception.CreatureNotFoundException;
import com.scorchedcode.ArkDinoBot.exception.MemberRegistrationException;
import com.scorchedcode.ArkDinoBot.exception.StatNotFoundException;

import java.util.Arrays;
import java.util.HashMap;

public class StatsRegistrar {
    private HashMap<CreatureTypes, HashMap<DinoStats, Integer>> registered = new HashMap<>();
    private static StatsRegistrar instance;

    private StatsRegistrar() {

    }

    public static StatsRegistrar getInstance() {
        if(instance == null)
            instance = new StatsRegistrar();
        return instance;
    }

    public void addStat(String id, String mob, String stat, Integer number) throws CreatureNotFoundException, MemberRegistrationException, StatNotFoundException, CreatureExistanceException {
        CreatureTypes creature = Util.acceptableName(mob);
        DinoStats statistic = Util.acceptableStat(stat);
        BreederRegistrar registrar = BreederRegistrar.getInstance();
        if(id == null || Arrays.asList(registrar.getCreatures(id)).contains(creature)) {
            HashMap<DinoStats, Integer> newMap = (registered.get(creature) != null ? registered.get(creature) : new HashMap<>());
            newMap.put(statistic, number);
            registered.put(creature, newMap);
            return;
        }
        throw new CreatureExistanceException(false);
    }

    public HashMap<DinoStats, Integer> listStats(String mob) throws CreatureNotFoundException, CreatureExistanceException {
        CreatureTypes creature = Util.acceptableName(mob);
        if(registered.keySet().contains(creature))
            return registered.get(creature);
        throw new CreatureExistanceException(false);
    }

    protected HashMap<CreatureTypes, HashMap<DinoStats, Integer>> listAllStats() {
        return registered;
    }

}
