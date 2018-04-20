package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;

import java.util.Locale;


/**
 * Created by BB on 15/11/2017.
 *
 * Used to draw the Health bars shown on screen.
 *
 * Also to create an 'effect' where after being hit white health is shown and then decreased over time.
 *
 *
 */

public class HealthBarSystem extends EntityProcessingSystem {

    private TileSystem tileSystem;

    private float initialHealthBarHeight = Measure.units(0.75f);
    private float initialHealthBarOffsetY = Measure.units(-1.5f);

    private final float whiteHealthBarSpeed = Measure.units(25f);
    private final float redHealthBarSpeed = Measure.units(80f);

    private Color bottomBarColor = new Color(Color.BLACK);
    private Color middleBarColor = new Color(Color.WHITE);
    private Color topBarColor = new Color(Color.RED);

    private Color healthTextColor = new Color(Color.WHITE);


    private SpriteBatch batch;

    private TextureAtlas atlas;

    private BitmapFont healthFont;
    private GlyphLayout glyphLayout = new GlyphLayout();


    private ObjectMap<Entity, HealthBar> entityHealthBarObjectMap = new ObjectMap<Entity, HealthBar>();

    public HealthBarSystem(MainGame game, Viewport gameport) {
        super(Aspect.all(HealthComponent.class, PositionComponent.class, CenteringBoundaryComponent.class, CoordinateComponent.class));
        this.batch = game.batch;
        this.atlas = game.assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
        healthFont = game.assetManager.get(Fonts.SMALL, BitmapFont.class);
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }




    //TODO store and then draw elsewhere.
    public void draw(Entity e){

        if(entityHealthBarObjectMap.containsKey(e)) {
         HealthBar hb = entityHealthBarObjectMap.get(e);

/*            batch.setColor(bottomBarColor);
            batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                    x, y,
                    initialHealthBarWidth,
                    initialHealthBarHeight);


            batch.setColor(middleBarColor);
            batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                    x, y,
                    healthBar.whiteHealthBarLength,
                    initialHealthBarHeight);


            batch.setColor(topBarColor);
            batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                    x, y,
                    healthBar.redHealthBarLength,
                    initialHealthBarHeight);*/


        }



    }


    @Override
    protected void process(Entity e) {


        Rectangle rect = tileSystem.getRectangleUsingCoordinates(e.getComponent(CoordinateComponent.class).coordinates);

        PositionComponent positionComponent = e.getComponent(PositionComponent.class);
        HealthComponent healthComponent = e.getComponent(HealthComponent.class);
        HealthBar healthBar = entityHealthBarObjectMap.get(e);

        float maxHealth = healthComponent.maxHealth;
        float health = healthComponent.health;

        //TODO The recetangle should instead by the cell size and you need to use the center coordinate of the CentaryBoundary
        //TODO to draw the health bar

        //Black bar
        float width = ((rect.getWidth() / 5) * 3.5f);

        float currentHealthBarWidth = (health / maxHealth) * width;
        float offsetX = rect.getWidth() / 5;


        //Sets up the red health bar. If the red health bar is less than the actual player's health
        //It increases in size until it reaches the correct length.

        if (healthBar.redHealthBarLength >= currentHealthBarWidth) { //The red health bar can not be greater than the player's health
            healthBar.redHealthBarLength = currentHealthBarWidth;
        } else if (healthBar.redHealthBarLength < currentHealthBarWidth) {
            healthBar.redHealthBarLength = currentHealthBarWidth; //redHealthBarSpeed * world.delta;
            if (healthBar.redHealthBarLength > currentHealthBarWidth)
                healthBar.redHealthBarLength = currentHealthBarWidth;
        }


        if (healthBar.whiteHealthBarLength < healthBar.redHealthBarLength) { //The white health bar can not be less than the red
            healthBar.whiteHealthBarLength = healthBar.redHealthBarLength;
            healthBar.whiteHealthBarTimer = healthBar.whiteHealthBarResetTimer;
        } else if (healthBar.whiteHealthBarLength >= healthBar.redHealthBarLength) {

            //The white health bar decreases only after period of time to return the red health bar's length.
            healthBar.whiteHealthBarTimer -= world.delta;

            if (healthBar.whiteHealthBarTimer <= 0) {
                healthBar.whiteHealthBarLength -= whiteHealthBarSpeed * world.delta;
            }

        }

        CenteringBoundaryComponent centeringBoundaryComponent = e.getComponent(CenteringBoundaryComponent.class);

        float x = rect.getX() + offsetX; //positionComponent.getX() + CenterMath.offsetX(centeringBoundaryComponent.bound.getWidth(), rect.getWidth());
        float y = rect.getY(); //positionComponent.getY() + initialHealthBarOffsetY;

        batch.setColor(bottomBarColor);
        batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                x, y,
                width,
                initialHealthBarHeight);


        batch.setColor(middleBarColor);
        batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                x, y,
                healthBar.whiteHealthBarLength,
                initialHealthBarHeight);


        batch.setColor(topBarColor);
        batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                x, y,
                healthBar.redHealthBarLength,
                initialHealthBarHeight);


        batch.setColor(topBarColor);
        batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                x, y,
                healthBar.redHealthBarLength,
                initialHealthBarHeight);

        glyphLayout.setText(healthFont, String.format(Locale.ENGLISH, "%s", (int) health), healthTextColor, rect.getWidth() / 5, Align.center, false);

        BitmapFontCache bitmapFontCache = new BitmapFontCache(healthFont);

        bitmapFontCache.addText(glyphLayout, rect.getX(),
                rect.getY() + glyphLayout.height + CenterMath.offsetY(rect.getWidth() / 5, glyphLayout.height));

        //applyHighlightToText(e, bitmapFontCache, textDescription.getText());

        bitmapFontCache.draw(batch);



    }


    @Override
    public void inserted(Entity e) {
        entityHealthBarObjectMap.put(e, new HealthBar());
    }

    @Override
    public void removed(Entity e) {
        entityHealthBarObjectMap.remove(e);
    }


    @Override
    protected void begin() {
        if (!batch.isDrawing()) {
            batch.begin();
        }
    }

    @Override
    protected void end() {
        batch.end();
    }


    private class HealthBar {

        public float redHealthBarLength;
        public float whiteHealthBarLength;

        public float whiteHealthBarTimer = 0.5f;
        public final float whiteHealthBarResetTimer = 0.5f;

        public float fadeTimer;
        public final float fadeTimerResetTime = 1.5f;

        public float alpha = 0;


    }


}
