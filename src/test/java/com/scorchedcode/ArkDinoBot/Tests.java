package com.scorchedcode.ArkDinoBot;

import static org.junit.Assert.*;

import com.scorchedcode.ArkDinoBot.exception.CreatureExistanceException;
import com.scorchedcode.ArkDinoBot.exception.CreatureNotFoundException;
import com.scorchedcode.ArkDinoBot.exception.MemberRegistrationException;
import org.junit.Test;

public class Tests {

    @Test public void testDoesChromedriverHaveProperName() {
        String name = Util.getChromedriverName();
        assertEquals("Chromedriver for Linux", "chromedriver", name);
    }

    @Test public void testCanExtractDinoNameAndNumber() {
        String input = Util.getDinoAndAmount("Darklust has submitted a request for 5 Spinosaur. Type !accept 5bd69fe3-06a6-4df9-ac57-dec59540ba91 to be assigned!");
        assertEquals("Number of dinos and name", "5 Spinosaur", input);
    }

    @Test public void testAddMemberToRegistrarWhileMemberAlreadyExists() {
        BreederRegistrar testRegistrar = BreederRegistrar.getInstance(); //
        String reason = "";
        try {
            testRegistrar.addMember("283701203092830");
            testRegistrar.addMember("283701203092830"); //Act
        } catch (MemberRegistrationException e) {
            reason = e.getReason();
        }
        assertEquals("Should state member already added", "This member is already registered", reason);
    }
    @Test public void testRemoveMemberFromRegistrarWhileMemberDoesNotExist() {
        BreederRegistrar testRegistrar = BreederRegistrar.getInstance(); //
        String reason = "";
        try {
            testRegistrar.removeMember("283701203092835"); //Act
        } catch (MemberRegistrationException e) {
            reason = e.getReason();
        }
        assertEquals("Should state member not added", "This member is not registered yet", reason);
    }

    @Test public void testAddCreatureThatDoesNotExistToRegistrar() {
        BreederRegistrar testRegistrar = BreederRegistrar.getInstance(); //
        String reason = "";
        try {
            testRegistrar.addMember("283701203092831"); //Act
            testRegistrar.addCreature("283701203092831", "something fucked");
        } catch (CreatureNotFoundException e) {
            reason = e.getReason();
        } catch (MemberRegistrationException e) {
            reason = e.getReason();
        } catch (CreatureExistanceException e) {
            reason = e.getReason();
        }
        assertEquals("Should state creature not found", "This is not a valid creature!", reason);
    }

    @Test public void testAddingCreatureByFriendlyName() {
        BreederRegistrar testRegistrar = BreederRegistrar.getInstance(); //
        CreatureTypes[] creatures = new CreatureTypes[0];
        try {
            testRegistrar.addMember("283701203092832"); //Act
            testRegistrar.addCreature("283701203092832", "zombie fire wyvern");
            creatures  = testRegistrar.getCreatures("283701203092832");
        } catch (CreatureNotFoundException e) {

        } catch (MemberRegistrationException e) {

        } catch (CreatureExistanceException e) {

        }
        assertEquals("Should state Zombie Fire Wyvern", CreatureTypes.Zombie_Fire_Wyvern, creatures[0]);
    }
}
