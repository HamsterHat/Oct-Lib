package octlib.expand.entities.abilities;

import arc.util.Time;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.gen.Groups;
import mindustry.gen.TimedKillUnit;
import mindustry.gen.Unit;
import mindustry.entities.abilities.Ability;


public class InfinityAbility extends Ability {
    public float range = 160f;
    public float infinityMultiplier = 0.1f;

    public InfinityAbility() {
    }

    public InfinityAbility(float range, float infinityMultiplier) {
        this.range = range;
        this.infinityMultiplier = infinityMultiplier;
    }



    @Override
    public void update(Unit unit) {
        if (unit == null || unit.dead) return;

        Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2, range * 2, bullet -> {
            if (bullet.team != unit.team && bullet.within(unit, range)) {
                bullet.vel.mul(infinityMultiplier * Time.delta);
            }
        });

        Groups.unit.intersect(unit.x - range, unit.y - range, range * 2, range * 2, other -> {
            if (other.team != unit.team && other instanceof TimedKillUnit && other.within(unit, range)) {
                other.vel.mul(infinityMultiplier * Time.delta);
            }
        });
    }

}
