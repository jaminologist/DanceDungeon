package com.bryjamin.dancedungeon.screens.strategy.worlds;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.factories.enemy.DummyFactory;
import com.bryjamin.dancedungeon.factories.enemy.RangedDummyFactory;
import com.bryjamin.dancedungeon.factories.player.PlayerFactory;
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.screens.battle.BattleDetails;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 17/12/2017.
 */

public class MapWorld extends WorldContainer {


    private Array<ComponentBag> playerParty = new Array<ComponentBag>();

    //BattleScreen battleScreen;
    private VictoryAdapter adapter;



    public MapWorld(MainGame game, Viewport gameport) {
        super(game, gameport);
        this.adapter = new VictoryAdapter();

        playerParty.add(new PlayerFactory().player(0,0, new Coordinates()));
        playerParty.add(new PlayerFactory().player(0,0, new Coordinates()));
        playerParty.add(null);
        playerParty.add(new PlayerFactory().player2(0,0, new Coordinates()));


        createWorld();


    }








    private void createWorld(){

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,
                        new MovementSystem(),
                        new UpdatePositionSystem(),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ConditionalActionSystem(),
                        new ParentChildSystem(),
                        new ExpireSystem(),
                        new DeathSystem()
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new ActionOnTapSystem(gameport),
                        new FadeSystem(),
                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch))
                .build();

        world = new World(config);

        float width = Measure.units(15f);
        float height = Measure.units(7.5f);

        Entity startButton = world.createEntity();
        startButton.edit().add(new PositionComponent(CenterMath.offsetX(gameport.getWorldWidth(), width), CenterMath.offsetY(gameport.getWorldHeight(), height) - Measure.units(5f)));
        startButton.edit().add(new HitBoxComponent(new HitBox(width, height)));
        startButton.edit().add(new CenteringBoundaryComponent(new Rectangle(0,0, width, height)));
        startButton.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .width(width)
                        .height(height).build(),
                new TextDescription.Builder(FileStrings.DEFAULT_FONT_NAME)
                        .text(TextResource.GAME_TITLE_START)
                        .color(new Color(Color.BLACK))
                        .build()));
        startButton.edit().add(new ActionOnTapComponent(new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                //game.getScreen().dispose();

                BattleDetails battleDetails = new BattleDetails();
                battleDetails.setPlayerParty(playerParty);
                battleDetails.getEnemyParty().add(new DummyFactory().targetDummyWalker(0,0));
                battleDetails.getEnemyParty().add(new DummyFactory().targetDummyWalker(0,0));
                battleDetails.getEnemyParty().add(new DummyFactory().targetDummySprinter(0,0));
                battleDetails.getEnemyParty().add(new RangedDummyFactory().rangedDummy(0,0));



                game.setScreen(new BattleScreen(game, game.getScreen(), battleDetails));
            }
        }));



    }


    @Override
    public void handleInput(InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(adapter);
    }

    private class VictoryAdapter extends InputAdapter {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 input = gameport.unproject(new Vector3(screenX, screenY, 0));
            if(world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)){
                return  true;
            };
            return false;
        }
    }


    public class StrategyMap {





    }


}