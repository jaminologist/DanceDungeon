package com.bryjamin.dancedungeon.screens.battle;

import com.bryjamin.dancedungeon.factories.player.UnitData;

/**
 * Created by BB on 17/12/2017.
 */

public class PartyDetails {

    public int money;
    public int grenades;
    public int medicalSupplies;

    private UnitData[] party = new UnitData[4];

    public void addPartyMember(UnitData unitData, int position){
        if(position - 1 > party.length) throw new RuntimeException("Not place for the party member");
        party[position] = unitData;
    };

    public UnitData[] getParty() {
        return party;
    }
}
