package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 18/11/2017.
 */

public abstract class SkillDescription {

    protected Spell spell;

    public abstract void createTargeting(World world, Entity player);

    public abstract boolean canCast(World world, Entity entity);

    public abstract void cast(World world, Entity entity, Coordinates target);

    public Spell getSpell(){
        return spell;
    }

    public abstract void endTurnUpdate();

    public abstract String getIcon();


}





