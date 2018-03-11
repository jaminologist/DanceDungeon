package com.bryjamin.dancedungeon.factories.spells.animations;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.OnDeathActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 24/12/2017.
 *
 * Class for building a projectile that travels in a straight line towards it target
 *
 * What it does upon reaching the target (death) is defined outside this class
 *
 */

public class BasicProjectile {


    private float width;
    private float height;

    private float damage;

    private float speed;

    private DrawableComponent drawableComponent;
    private Skill skill;


    public BasicProjectile(BasicProjectileBuilder b){
        this.width = b.width;
        this.height = b.height;
        this.damage = b.damage;
        this.drawableComponent = b.drawableComponent;
        this.speed = b.speed;
        this.skill = b.skill;
    }


    public static class BasicProjectileBuilder {

        private float width;
        private float height;

        private float damage;

        private float speed = Measure.units(60f);

        private DrawableComponent drawableComponent = new DrawableComponent();
        private Skill skill = new Skill(new Skill.Builder());

        public BasicProjectileBuilder width(float val)
        { width = val; return this; }

        public BasicProjectileBuilder height(float val)
        { height = val; return this; }

        public BasicProjectileBuilder damage(float val)
        { damage = val; return this; }

        public BasicProjectileBuilder speed(float val)
        { speed = val; return this; }

        public BasicProjectileBuilder drawableComponent(DrawableComponent val)
        { drawableComponent = val; return this; }

        public BasicProjectileBuilder skill(Skill val){
            skill = val; return this;
        }

        public BasicProjectile build()
        { return new BasicProjectile(this); }

    }


   public void cast(World world, final Entity user, final Coordinates target){

       PositionComponent positionComponent = user.getComponent(PositionComponent.class);
       CenteringBoundaryComponent cbc = user.getComponent(CenteringBoundaryComponent.class);

       float x = CenterMath.centerOnPositionX(width, cbc.bound.getX() + cbc.bound.getWidth() / 2);
       float y = CenterMath.centerOnPositionY(height, cbc.bound.getY() + cbc.bound.getHeight() / 2);


       Rectangle r = world.getSystem(TileSystem.class).createRectangleUsingCoordinates(target);

       Entity projectile = world.createEntity();
       projectile.edit().add(new PositionComponent(x, y));
       world.getSystem(ActionCameraSystem.class).createDeathWaitAction(projectile);

       projectile.edit().add(new MoveToComponent(speed, new Vector3(
               CenterMath.centerOnPositionX(width, r.getCenter(new Vector2()).x),
               CenterMath.centerOnPositionY(height, r.getCenter(new Vector2()).y),
               0)));

       projectile.edit().add(drawableComponent);


       projectile.edit().add(new VelocityComponent());
       projectile.edit().add(new CenteringBoundaryComponent(width, height));

       projectile.edit().add(new ConditionalActionsComponent(new WorldConditionalAction() {
           @Override
           public boolean condition(World world, Entity entity) {
               return entity.getComponent(MoveToComponent.class).isEmpty();
           }

           @Override
           public void performAction(World world, Entity entity) {
               entity.edit().remove(ConditionalActionsComponent.class);
               entity.edit().add(new DeadComponent());
           }
       }));


       projectile.edit().add(new OnDeathActionsComponent(new WorldAction() {
           @Override
           public void performAction(World world, Entity entity) {
               skill.castSpellOnTargetLocation(world, user, target);
           }
       }));







   }












}
