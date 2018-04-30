package com.bryjamin.dancedungeon.factories.map.event;

import com.bryjamin.dancedungeon.assets.MapData;
import com.bryjamin.dancedungeon.factories.map.event.objectives.SurviveObjective;

public class TutorialEvent extends BattleEvent{

    public TutorialEvent(){

        super(new Builder(MapData.MAP_TUTORIAL).primaryObjective(new SurviveObjective(5)));

    }


}