package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;

/**
 * Created by BB on 28/10/2017.
 */

public class FadeSystem extends EntityProcessingSystem {

    ComponentMapper<FadeComponent> fm;
    ComponentMapper<DrawableComponent> drawableMapper;

    @SuppressWarnings("unchecked")
    public FadeSystem() {
        super(Aspect.all(FadeComponent.class, DrawableComponent.class));
    }

    @Override
    protected void process(Entity e) {

        FadeComponent fc = fm.get(e);

        if (fc.flicker) {
            applyFade(e, fc.maxAlpha);
            fc.flicker = false;
            return;
        }

        fc.alphaTimer = fc.fadeIn ? fc.alphaTimer + world.delta : fc.alphaTimer - world.delta;

        fc.alpha = Interpolation.fade.apply(((fc.alphaTimer / fc.alphaTimeLimit) * (fc.maxAlpha - fc.minAlpha)) + fc.minAlpha);
        if (fc.alpha <= fc.minAlpha) {
            if (fc.isEndless || fc.count > 0) {
                fc.fadeIn = true;
                fc.alpha = fc.minAlpha;
                fc.count--;

            } else {
                fc.alpha = fc.minAlpha;
                //fc.alphaTimer = 0;
            }
        } else if (fc.alpha >= fc.maxAlpha) {
            if (fc.isEndless || fc.count > 0) {
                fc.fadeIn = false;
                fc.alpha = fc.maxAlpha;
                fc.count--;

            } else {
                fc.alpha = fc.maxAlpha;
                //fc.alphaTimer = fc.alphaTimeLimit;
            }
        }

        applyFade(e, fc.alpha);

    }

    private void applyFade(Entity e, float alpha) {

        if (drawableMapper.has(e)) {
            DrawableComponent dc = drawableMapper.get(e);
            for (DrawableDescription drawableDescription : dc.drawables) {
                drawableDescription.getColor().a = alpha;
            }
        }
    }

}

