package com.cmaher.game.components;

import java.util.Set;

import com.cmaher.game.FactionType;
import com.cmaher.game.entity.EntityBase;
import com.cmaher.game.entity.Factionable;

/**
 * TODO: Fix this. This is the result of the engine being poorly hacked together
 * in a small amount of time and should be made nicer. Also, fix factionable in
 * general
 * 
 * @author Christian
 * 
 */
public class FactionableCollisionComponent extends RadialCollisionComponent {

    public final Factionable fEntity;
    private FactionType      type;
    private FactionType      unfriendly;
    private FactionType      unfriendlyBullet;

    public FactionableCollisionComponent(Factionable master,
            PlaceComponent place, FactionType type, FactionType unfriendly,
            FactionType unfriendlyBullet) {
        super((EntityBase) master, place);
        this.type = type;
        this.fEntity = master;
        this.unfriendly = unfriendly;
        this.unfriendlyBullet = unfriendlyBullet;
    }

    public void update(float delta) {
        super.update(delta);
        Set<CollisionComponent> collisions = master.getGame()
                .getCollisionManager().getCollisions(this);

        for (CollisionComponent rcc : collisions) {
            //only collide with factions
            if (rcc instanceof FactionableCollisionComponent) {
                FactionableCollisionComponent collision = (FactionableCollisionComponent) rcc;

                if (!master.getGame().getCollisionManager()
                        .isResolved(this, collision)) {
                    if (collision.getType().equals(unfriendly)) {
                        fEntity.collideUnfriendly((Factionable) collision.master);
                        oppositeCollide((Factionable) collision.master);

                        master.getGame().getCollisionManager()
                                .setResolved(this, collision);
                    } else if (collision.getType().equals(unfriendlyBullet)) {
                        fEntity.collideUnfriendlyBullet((Factionable) collision.master);
                        master.getGame().getCollisionManager()
                                .setResolved(this, collision);
                        oppositeCollide((Factionable) collision.master);
                    }
                }
            }
        }
    }

    private void oppositeCollide(Factionable faction) {
        if (getType().equals(FactionType.PlayerBullet)
                || getType().equals(FactionType.EnemyBullet)) {
            faction.collideUnfriendlyBullet(fEntity);
        } else if (getType().equals(FactionType.Player)
                || getType().equals(FactionType.Enemy)) {
            faction.collideUnfriendly(fEntity);
        }
    }

    public FactionType getType() {
        return type;
    }

    public void setType(FactionType type) {
        this.type = type;
    }
}
