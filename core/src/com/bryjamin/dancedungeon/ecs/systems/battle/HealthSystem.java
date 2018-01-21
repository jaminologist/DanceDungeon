package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.ExpireComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;

/**
 * Created by BB on 15/10/2017.
 */

public class HealthSystem extends EntityProcessingSystem {

    ComponentMapper<HealthComponent> healthm;
    ComponentMapper<VelocityComponent> vm;
    ComponentMapper<PlayerControlledComponent> pm;
    ComponentMapper<BlinkOnHitComponent> blinkOnHitMapper;

    @SuppressWarnings("unchecked")
    public HealthSystem() {
        super(Aspect.all(HealthComponent.class));
    }


    @Override
    protected void process(Entity e) {

        HealthComponent hc = healthm.get(e);


        if(hc.getAccumulatedDamage() > 0 && blinkOnHitMapper.has(e)) {
            blinkOnHitMapper.get(e).isHit = true;
            //TODO I grab parts of entity that aren't called in the Aspect class, so there is a null pointer chance
            createFloatingDamageText(world, (int) hc.getAccumulatedDamage(), new Color(Color.RED), e);
        }

        if(hc.getAccumulatedHealing() > 0 && blinkOnHitMapper.has(e)) {
           // blinkOnHitMapper.get(e).isHit = true;
            //TODO I grab parts of entity that aren't called in the Aspect class, so there is a null pointer chance
            createFloatingDamageText(world, (int) hc.getAccumulatedHealing(), new Color(Color.GREEN), e);
        }


        hc.health = hc.health + hc.getAccumulatedHealing() > hc.maxHealth ? hc.maxHealth : hc.health + hc.getAccumulatedHealing();
        hc.clearHealing();
        hc.health = hc.health - hc.getAccumulatedDamage();
        hc.clearDamage();
        if(hc.health <= 0) e.edit().add(new DeadComponent());


    }



    public void createFloatingDamageText(World world, int number, Color color, Entity entity){

        Entity floatingTextEntity = world.createEntity();

        floatingTextEntity.edit().add(new PositionComponent(entity.getComponent(PositionComponent.class)));
        floatingTextEntity.edit().add(new CenteringBoundaryComponent(entity.getComponent(CenteringBoundaryComponent.class)));
        floatingTextEntity.edit().add(new FadeComponent.FadeBuilder()
                .maximumTime(0.75f)
                .endless(false)
                .fadeIn(false)
                .build());
        floatingTextEntity.edit().add(new VelocityComponent(0, Measure.units(20f)));
        floatingTextEntity.edit().add(new ExpireComponent(2.0f));
        floatingTextEntity.edit().add(new DrawableComponent(Layer.BACKGROUND_LAYER_FAR,
                new TextDescription.Builder(Fonts.MEDIUM)
                        .text(Integer.toString(number))
                        .color(color)
                        .build()));



    }




}

