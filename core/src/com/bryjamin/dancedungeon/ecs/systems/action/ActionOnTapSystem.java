package com.bryjamin.dancedungeon.ecs.systems.action;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;

/**
 * Created by BB on 20/10/2017.
 */

public class ActionOnTapSystem extends EntitySystem {

    private Viewport gamePort;

    public ActionOnTapSystem(Viewport gamePort) {
        super(Aspect.all(ActionOnTapComponent.class, HitBoxComponent.class));
        this.gamePort = gamePort;
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    @Override
    protected void processSystem() {

    }


    /**
     * Runs a check to see if the inserted co-ordinates are contained in any of this system's entities'
     * collision boundaries. If they are an action is performed
     *
     * @param x - x position of the area of the screen that was touched
     * @param y - y position of the area of the screen that was touched
     * @return - True if an entity has been touched, False otherwise
     */
    public boolean touch(float x, float y){

        System.out.println(this.getEntities().size());


        for(Entity e : this.getEntities()) {

            ActionOnTapComponent actionOnTapComponent = e.getComponent(ActionOnTapComponent.class);

            if(!actionOnTapComponent.enabled) continue;
            if (e.getComponent(HitBoxComponent.class).contains(x, y)) {
                actionOnTapComponent.setTouchX(x);
                actionOnTapComponent.setTouchY(y);
                for(WorldAction wa : e.getComponent(ActionOnTapComponent.class).actions) wa.performAction(world, e);
                return true;
            }
        }
        return false;
    }

}
