package com.bryjamin.dancedungeon.ecs.systems.strategy;

import com.artemis.BaseSystem;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.map.event.MapEvent;
import com.bryjamin.dancedungeon.factories.map.event.RestEvent;
import com.bryjamin.dancedungeon.factories.map.event.TestEvent;

/**
 * Created by BB on 21/01/2018.
 */

public class EventGenerationSystem extends BaseSystem {

    @Override
    protected void processSystem() {}

    public MapEvent getMapEvent(MapEvent.EventType eventType){

        switch (eventType){
            case BATTLE:
            default:
                return new BattleEvent(EnemyFactory.FAST_BLOB);
            case REST:
                return new RestEvent();
            case SHOP:
                return new TestEvent();
        }


    }


}