package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MovementRangeComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.AttackAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.factories.player.spells.MovementDescription;
import com.bryjamin.dancedungeon.factories.player.spells.SkillDescription;

import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ALLY;
import static com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem.TURN.ENEMY;

/**
 * Created by BB on 21/10/2017.
 *
 * System used to keep track of player and enemy turns
 */

public class TurnSystem extends EntitySystem {

    private ComponentMapper<TurnComponent> turnMapper;

    private ComponentMapper<EnemyComponent> enemyMapper;
    private ComponentMapper<PlayerControlledComponent> playerMapper;

    private ComponentMapper<AttackAiComponent> attackAiComponentMapper;
    private ComponentMapper<MovementRangeComponent> movementRangeMapper;


    private ComponentMapper<AbilityPointComponent> abilityPointMapper;


    private ComponentMapper<SkillsComponent> skillMapper;


    private ComponentMapper<UtilityAiComponent> utilityAiMapper;

    private ComponentMapper<CoordinateComponent> coordinateMapper;

    private Array<Entity> currentTurnEntities = new Array<Entity>();

    private Entity currentEntity;

    private Array<Entity> enemyTurnEntities = new Array<Entity>();
    private Array<Entity> allyTurnEntities = new Array<Entity>();

    private TurnComponent currentTurnComponent;

    private boolean processingFlag = true;


    private enum STATE {
        WAITING, NEXT
    }

    private STATE state = STATE.NEXT;


    public enum TURN {
        ENEMY, ALLY
    }

    public TURN turn = ENEMY;

    @SuppressWarnings("unchecked")
    public TurnSystem() {
        super(Aspect.all(TurnComponent.class, SkillsComponent.class).one(UtilityAiComponent.class, PlayerControlledComponent.class));
    }


    @Override
    public void inserted(Entity e) {

        if (enemyMapper.has(e)) {
            enemyTurnEntities.add(e);
        } else if (playerMapper.has(e)) {
            allyTurnEntities.add(e);
        }

    }

    @Override
    public void removed(Entity e) {
        enemyTurnEntities.removeValue(e, true);
        allyTurnEntities.removeValue(e, true);
        currentTurnEntities.removeValue(e, true);

        try {
            if (currentEntity.equals(e)) {
                state = STATE.NEXT;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void setUp(TURN turn) {

        this.turn = turn;
        currentTurnEntities.clear();

        if (turn == ENEMY) {
            currentTurnEntities.addAll(enemyTurnEntities);
        } else if (turn == ALLY) {
            currentTurnEntities.addAll(allyTurnEntities);
        }

        state = STATE.NEXT;

        processingFlag = true;


    }


    @Override
    protected boolean checkProcessing() {
        return processingFlag;
    }

    //TODO organise, as it is quite messy
    @Override
    protected void processSystem() {


        switch (state) {

            case NEXT:
                switch (turn) {

                    case ENEMY:


                        if (currentTurnEntities.size <= 0) {
                            setUp(ALLY);
                            return;
                        }


                        break;

                    case ALLY:

                        if (currentTurnEntities.size <= 0) {
                            setUp(ENEMY);
                            return;
                        }


                        // break;

                }


                currentEntity = currentTurnEntities.pop();
                if (!playerMapper.has(currentEntity)) {
                    currentEntity.getComponent(TurnComponent.class).state = TurnComponent.State.DECIDING;
                    AbilityPointComponent apc = abilityPointMapper.get(currentEntity);
                    apc.abilityPoints = apc.abilityPointsPerTurn;

                    if(skillMapper.has(currentEntity)){
                        skillMapper.get(currentEntity).endTurn();
                    }

                } else {

                    for(Entity e : allyTurnEntities){
                        skillMapper.get(e).endTurn();
                    }

                }


                state = STATE.WAITING;


                break;


            case WAITING:

                TurnComponent turnComponent = currentEntity.getComponent(TurnComponent.class);


                if (!playerMapper.has(currentEntity)) {

                    switch (turnComponent.state) {

                        case DECIDING:

                            //TODO change to just be a varaible that states the turn is over?

                            SkillsComponent skillsComponent = currentEntity.getComponent(SkillsComponent.class);

                            boolean continueTurn = false;


                            for(SkillDescription skillDescription : skillsComponent.skillDescriptions){

                                if(skillDescription instanceof MovementDescription){
                                    System.out.println("move skill + " + skillDescription.canCast(world, currentEntity));
                                }


                                continueTurn = skillDescription.canCast(world, currentEntity);
                                if(continueTurn) break;
                            }

                            if (!continueTurn) {
                                turnComponent.state = TurnComponent.State.END;
                            } else {

                                turnComponent.state = TurnComponent.State.WAITING;

                                if (utilityAiMapper.has(currentEntity)) {
                                    utilityAiMapper.get(currentEntity).utilityAiCalculator.performAction(world, currentEntity);
                                }

                            }

                            break;

                        case WAITING:

                            if (turnComponent.turnOverCondition.condition(world, currentEntity))
                                turnComponent.state = TurnComponent.State.DECIDING;

                            break;

                        case END:

                            state = STATE.NEXT;
                            break;
                    }

                }

                break;


        }

    }


}