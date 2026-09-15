package octlib.expand.entities.abilities;

import arc.util.Time;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.entities.abilities.Ability;


public class InfinityAbility extends Ability {
    public float range = 160f;
    public float pushForce = 4.5f;
    
    private static final Vec2 tmpVec = new Vec2();

    public InfinityAbility() {}

    public InfinityAbility(float range, float pushForce) {
        this.range = range;
        this.pushForce = pushForce;
    }



    @Override
    public void update(Unit unit) {
        if (unit == null || unit.dead) return;

        Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2, range * 2, bullet -> {
            if (bullet.team != unit.team && bullet.within(unit, range)) {
                float dst = bullet.dst(unit);
                if (dst <= 0.1f) return;

                float proximityFactor = 1.0f - (dst / range);


                tmpVec.set(bullet.x - unit.x, bullet.y - unit.y).nor();

                bullet.team = unit.team;

                bullet.vel.set(tmpVec).scl(pushForce * (1.0f + proximityFactor * 2f) * Time.delta);
                
                bullet.time -= 0.2f * Time.delta;
            }
        });

        Groups.unit.intersect(unit.x - range, unit.y - range, range * 2, range * 2, other -> {

            if (other.team != unit.team && !other.dead && other.within(unit, range)) {
                
                float dst = other.dst(unit);
                if (dst <= 0.1f) return;


                float proximityFactor = 1.0f - (dst / range);


                tmpVec.set(other.x - unit.x, other.y - unit.y).nor();

                other.vel.set(tmpVec).scl(pushForce * proximityFactor * Time.delta);
                

                if (dst < 35f) {
                    other.vel.add(tmpVec.scl(pushForce * 2f));
                }
            }
        });
    }

    @Override
    public void draw(Unit unit) {
        Color customPurple = Color.valueOf("8232fa");
        
        Draw.color(Color.purple, customPurple, Mathf.absin(Time.time, 6f, 0.4f));
        Lines.stroke(1.5f);
        
        Lines.circle(unit.x, unit.y, range);
        
        for (int i = 1; i <= 4; i++) {
            float sizeFactor = (Time.time * 0.015f + (i / 4f)) % 1f;
            Lines.circle(unit.x, unit.y, range * (1f - sizeFactor));
        }
        
        Draw.alpha(0.03f);
        Fill.circle(unit.x, unit.y, range);
        Draw.reset();
    }

}
