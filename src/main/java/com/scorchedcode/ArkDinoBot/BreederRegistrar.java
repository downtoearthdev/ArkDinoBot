package com.scorchedcode.ArkDinoBot;

import com.scorchedcode.ArkDinoBot.exception.CreatureExistanceException;
import com.scorchedcode.ArkDinoBot.exception.CreatureNotFoundException;
import com.scorchedcode.ArkDinoBot.exception.MemberRegistrationException;
import com.scorchedcode.ArkDinoBot.exception.ReasonableException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BreederRegistrar {
    private static BreederRegistrar instance;
    private HashMap<String, CreatureTypes[]> registered = new HashMap<>();

    private BreederRegistrar() {

    }

    public static BreederRegistrar getInstance() {
        if(instance == null)
            instance = new BreederRegistrar();
        return instance;
    }

    public void addMember(String id) throws MemberRegistrationException {
        if(!registered.containsKey(id))
            registered.put(id, new CreatureTypes[0]);
        else
            throw new MemberRegistrationException(true);
    }

    public void removeMember(String id) throws MemberRegistrationException {
        if(registered.containsKey(id))
            registered.remove(id);
        else
            throw new MemberRegistrationException(false);
    }

    public void addCreature(String id, String mob) throws MemberRegistrationException, CreatureExistanceException,CreatureNotFoundException{
        CreatureTypes creature = Util.acceptableName(mob);
        if(registered.containsKey(id)) {
            if(!Arrays.asList(registered.get(id)).contains(creature)) {
                ArrayList<CreatureTypes> creatures = new ArrayList<>(Arrays.asList(registered.get(id)));
                creatures.add(creature);
                registered.put(id, creatures.toArray(new CreatureTypes[creatures.size()]));
                return;
            }
            throw new CreatureExistanceException(true);
        }
        throw new MemberRegistrationException(false);
    }

    public void removeCreature(String id, String mob) throws MemberRegistrationException, CreatureExistanceException,CreatureNotFoundException{
        CreatureTypes creature = Util.acceptableName(mob);
        if(registered.containsKey(id)) {
            if(Arrays.asList(registered.get(id)).contains(creature)) {
                List<CreatureTypes> creatures = Arrays.asList(registered.get(id));
                creatures.add(creature);
                registered.remove(id, creatures.toArray(new CreatureTypes[creatures.size()]));
                return;
            }
            throw new CreatureExistanceException(false);
        }
        throw new MemberRegistrationException(false);
    }

    public CreatureTypes[] getCreatures(String id) throws MemberRegistrationException {
        if(registered.containsKey(id))
            return registered.get(id);
        throw new MemberRegistrationException(false);
    }

    public ArrayList<String> getBreeders() {
        return new ArrayList<>(registered.keySet());
    }

}
