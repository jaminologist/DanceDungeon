package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 28/01/2018.
 */

public class StunStrike extends Skill {

    public StunStrike() {
        super(new Builder()
                .name("Stun Strike")
                .description("Stuns a Target For 3 Rounds, Deals No Damage")
                .icon(TextureStrings.SKILLS_SLASH)
                .targeting(Targeting.Enemy)
                .spellCoolDown(3)
                .spellAnimation(Skill.SpellAnimation.Slash)
                .spellType(Skill.SpellType.PhysicalAttack)
                .spellEffects(SpellEffect.Stun)
                .attack(Attack.Ranged));

    }

}