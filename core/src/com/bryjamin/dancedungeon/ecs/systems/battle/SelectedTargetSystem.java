package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.FollowPositionComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SelectedEntityComponent;
import com.bryjamin.dancedungeon.factories.spells.SpellFactory;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 18/11/2017.
 * <p>
 * System that tracks which entity is currently selected by the blob.
 * <p>
 * It changes the buttons and text displayed on the screen based on the entity selected
 */

public class SelectedTargetSystem extends EntityProcessingSystem {

    private ComponentMapper<PlayerControlledComponent> playerControlledM;


    private Array<Entity> buttons = new Array<Entity>();


    private static final float infoX = Measure.units(2.5f);
    private static final float infoY = Measure.units(50f);
    private static final float infoSize = Measure.units(5f);

    private boolean processingFlag = false;


    public SelectedTargetSystem() {
        super(Aspect.all(SelectedEntityComponent.class));
    }

    @Override
    protected void process(Entity e) {

        if(!e.getComponent(TurnComponent.class).hasActions()){
            this.clear();
            e.edit().remove(SelectedEntityComponent.class);
        }



        //if()


    }


    @Override
    public void inserted(Entity e) {

        if(this.getEntities().size() > 1){
            this.clear();

            for(Entity old : this.getEntities()){
                if(!old.equals(e)) {
                    old.edit().remove(SelectedEntityComponent.class);
                }
            }

        }

        setUpCharacter(e);
    }

    @Override
    public void removed(Entity e) {
        if(this.getEntities().size() <= 0) {
            this.clear();
        }
    }




    @Override
    protected boolean checkProcessing() {
        return this.getEntities().size() > 0;
    }

    /**
     * Clears the button entities and selected entity from the system
     */
    public void clear() {
        for (Entity e : buttons) {
            //e.edit().add(new DeadComponent());
            e.deleteFromWorld();
        }
        buttons.clear();

        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(UITargetingComponent.class)).getEntities();

        for (int i = 0; i < bag.size(); i++) {
            //world.getEntity(bag.get(i)).edit().add(new DeadComponent());
            world.getEntity(bag.get(i)).deleteFromWorld();
        }

    }

    public void reset(){
        this.clear();

        for(Entity e : this.getEntities()){
            e.edit().remove(SelectedEntityComponent.class);
        }
    }



    /**
     * Checks the given x and y input to see if a playerable character has been selected.
     * If this is the case the system sets up the character's spells
     *
     * @param x - x input
     * @param y - y input
     * @return - True if a playerable character has been selected
     */
    public boolean selectCharacter(float x, float y) {

        if(world.getSystem(ActionCameraSystem.class).isProcessing()) return false;

        Coordinates c = world.getSystem(TileSystem.class).getCoordinatesUsingPosition(x, y);

        if (world.getSystem(TileSystem.class).getOccupiedMap().containsValue(c, false)) {
            world.getSystem(TileSystem.class).getOccupiedMap().findKey(c, false).edit().add(new SelectedEntityComponent());
            return true;
        } else {
            this.reset();
        }

        return false;
    }


    public void reselectEntityAfterActionComplete() {

        if(this.getEntities().size() > 0){

            Entity selectedEntity = this.getEntities().get(0);

            TurnComponent turnComponent = selectedEntity.getComponent(TurnComponent.class);
            if (turnComponent.movementActionAvailable || turnComponent.attackActionAvailable ||
                    selectedEntity.getComponent(SkillsComponent.class).canCast(world, selectedEntity)) {

                //if (entityArray.size > 0) {
                setUpCharacter(selectedEntity);
                //}

            } else {
                this.clear();
            }


        }

    }


    /**
     * Uses the given entity and sets up it's skills
     *
     * @param playableCharacter - Selected playable character
     */
    private void setUpCharacter(final Entity playableCharacter) {

        //Can't select a character with no actions

        //This only exists for players
        //if(playerControlledM.has(selectedEntity)) {
        if(playerControlledM.has(playableCharacter)) {
            if (!playableCharacter.getComponent(TurnComponent.class).hasActions()) return;
        }

        this.clear(); // Clear buttons and recticle before remaking them.

        createTargetReticle(world, playableCharacter);
        createUnitInformationEntity(world, playableCharacter);


        if(playerControlledM.has(playableCharacter)) {

            SkillsComponent skillsComponent = playableCharacter.getComponent(SkillsComponent.class);

            for (int i = 0; i < skillsComponent.skillDescriptions.size; i++) {
                buttons.add(BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().skillButton(Measure.units(25f) * (i + 1), 0,
                        skillsComponent.skillDescriptions.get(i), playableCharacter)));
            }

            createMovementAndAttackTiles(playableCharacter);
        }
    }


    /**
     * Upon being selected creates Movement and Attacking tiles for the player, based on
     * the avaliablity of an entites attack and movement actions
     *
     * @param e
     */
    private void createMovementAndAttackTiles(Entity e) {

        TurnComponent turnComponent = e.getComponent(TurnComponent.class);

        if (turnComponent.attackActionAvailable && turnComponent.movementActionAvailable) {
            new TargetingFactory().createMovementTiles(world, e, e.getComponent(StatComponent.class).movementRange);
        } else if (turnComponent.attackActionAvailable) {
            new TargetingFactory().createTargetTiles(world, e,
                    e.getComponent(SkillsComponent.class).basicAttack,
                    e.getComponent(StatComponent.class).attackRange);
        }


    }

    private void createTargetReticle(World world, Entity entity){

        float width = entity.getComponent(CenteringBoundaryComponent.class).bound.width * 2.5f;
        float height = entity.getComponent(CenteringBoundaryComponent.class).bound.height * 2.5f;

        Entity recticle = world.createEntity();
        recticle.edit().add(new PositionComponent());
        recticle.edit().add(new UITargetingComponent());
        recticle.edit().add(new FollowPositionComponent(entity.getComponent(PositionComponent.class).position,
                CenterMath.offsetX(entity.getComponent(CenteringBoundaryComponent.class).bound.width, width),
                CenterMath.offsetY(entity.getComponent(CenteringBoundaryComponent.class).bound.height, height)
        ));
        recticle.edit().add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.TARGETING)
                .width(width)
                .height(height)
                .color(playerControlledM.has(entity) ? new Color(Color.WHITE) : new Color(Color.RED))
                .build()));

    }


    public void createUnitInformationEntity(World world, Entity entity) {

        StatComponent stats = entity.getComponent(StatComponent.class);
        HealthComponent health = entity.getComponent(HealthComponent.class);

        Entity info = world.createEntity();
        info.edit().add(new PositionComponent(infoX, infoY));
        info.edit().add(new CenteringBoundaryComponent(infoSize, infoSize));
        info.edit().add(new DrawableComponent(entity.getComponent(DrawableComponent.class)));
        info.edit().add(new UITargetingComponent());


        Entity hpText = world.createEntity();
        hpText.edit().add(new PositionComponent(infoX, infoY - infoSize));
        hpText.edit().add(new CenteringBoundaryComponent(infoSize, infoSize));
        hpText.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                new TextDescription.Builder(Fonts.SMALL)
                        .color(new Color(Color.WHITE))
                        .text("HP " + (int) health.health + "/" + stats.maxHealth)
                        .build()));
        hpText.edit().add(new UITargetingComponent());

/*

        Entity hpTextAgain = world.createEntity();
        hpTextAgain.edit().add(new PositionComponent(infoX, infoY - infoSize * 1.5f));
        hpTextAgain.edit().add(new CenteringBoundaryComponent(infoSize, infoSize));
        hpTextAgain.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                new TextDescription.Builder(Fonts.SMALL)
                        .color(new Color(Color.WHITE))
                        .text((int) health.health + "/" + stats.healthAndMax)
                        .build()));
        hpTextAgain.edit().add(new UITargetingComponent());
*/


    }



}
