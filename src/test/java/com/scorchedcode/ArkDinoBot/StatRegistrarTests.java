package com.scorchedcode.ArkDinoBot;

import static org.junit.Assert.*;

import com.scorchedcode.ArkDinoBot.exception.CreatureExistanceException;
import com.scorchedcode.ArkDinoBot.exception.CreatureNotFoundException;
import com.scorchedcode.ArkDinoBot.exception.MemberRegistrationException;
import com.scorchedcode.ArkDinoBot.exception.StatNotFoundException;
import org.junit.Test;

import java.util.HashMap;

public class StatRegistrarTests {

    @Test public void testCanAddRealCreature() throws CreatureNotFoundException, CreatureExistanceException, MemberRegistrationException, StatNotFoundException {
        BreederRegistrar registrar = BreederRegistrar.getInstance();
        registrar.addMember("223798329832");
        registrar.addCreature("223798329832", "zombie fire w");
        StatsRegistrar sregistrar = StatsRegistrar.getInstance();
        sregistrar.addStat("223798329832", "Zombie Fire Wyvern", "HEALTH", 5);
        HashMap<DinoStats, Integer> testMap = new HashMap<>();
        testMap.put(DinoStats.HEALTH, 5);
        assertEquals(testMap.get(DinoStats.HEALTH), sregistrar.listStats("Zombie Fire Wyvern").get(DinoStats.HEALTH));
    }
}
