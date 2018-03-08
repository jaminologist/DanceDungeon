package com.bryjamin.dancedungeon.factories.player;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.BuffComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.PushabaleComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SolidComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 23/12/2017.
 */

public class UnitFactory {

    public Entity baseTileBag(World world, Coordinates c){

        Entity e = world.createEntity();

        e.edit().add(new PositionComponent())
                .add(new SolidComponent())
                .add(new PushabaleComponent())
                .add(new CoordinateComponent(c))
                .add(new VelocityComponent())
                .add(new SolidComponent())
                .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.BLOCK)
                        .size(Measure.units(5f)).build()))
                .add(new CenteringBoundaryComponent(new Rectangle(0, 0, Measure.units(3f), Measure.units(3f))));
/*
        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent());
        bag.add(new UnitComponent(unitData));
        bag.add(new SolidComponent());

        bag.add(new HealthComponent(unitData.statComponent.health, unitData.statComponent.maxHealth));
        bag.add(new CoordinateComponent());
        bag.add(new MoveToComponent(Measure.units(60f))); //TODO speed should be based on the class
        bag.add(new VelocityComponent());

        //Graphical
        bag.add(new BlinkOnHitComponent());
        bag.add(unitData.statComponent);
        bag.add(new TurnComponent());*/

        return e;

    }



    public ComponentBag baseUnitBag(UnitData unitData){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent());
        bag.add(new UnitComponent(unitData));
        bag.add(new SolidComponent());

        bag.add(new HealthComponent(unitData.statComponent.health, unitData.statComponent.maxHealth));
        bag.add(new CoordinateComponent());
        bag.add(new MoveToComponent(Measure.units(60f))); //TODO speed should be based on the class
        bag.add(new VelocityComponent());
        bag.add(new TargetComponent());
        bag.add(new BuffComponent());

        //Graphical
        bag.add(new BlinkOnHitComponent());
        bag.add(unitData.statComponent);
        bag.add(new TurnComponent());

        return bag;

    }


    public ComponentBag basePlayerUnitBag(UnitData unitData){
        ComponentBag bag = baseUnitBag(unitData);
        bag.add(new PlayerControlledComponent());
        return bag;
    }

    public ComponentBag baseEnemyUnitBag(UnitData unitData){
        ComponentBag bag = baseUnitBag(unitData);
        bag.add(new EnemyComponent());
        return bag;
    }












}
