package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 18/11/2017.
 */

public class FireballDescription extends CooldownSpellDescription {


    public FireballDescription(){
        spell = new Fireball(6);
    }

    @Override
    public void createTargeting(World world, final Entity player) {
        new TargetingFactory().createTargetTiles(world, player, this, 3);
    }

    @Override
    public String getIcon() {
        return "skills/Fire";
    }


}
