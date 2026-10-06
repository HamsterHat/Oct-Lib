package octlib.expand.entities.status;

import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.StatUnit;
import octlib.expand.meta.OctoStat;
import arc.util.Time;

import static octlib.expand.meta.*;

public class KarmaEffect extends StatusEffect {
    public float percentDamage = 0.1f / 60f;
    public float karmaFloor = 1f;

   public KarmaEffect(String name) {
       super(name);
   }
   

    @Override
    public void setStats() {
        super.setStats();
        stats.add(OctoStat.KR, percentDamage * 60f * 100f, StatUnit.perSecond);
    }

    @Override
    public void update(Unit unit, float time){
        super.update(unit, time);
        
        float calculatedDamage = unit.health * percentDamage * Time.delta;

        if (unit.health <= karmaFloor || (unit.health - calculatedDamage) <= karmaFloor) {
            unit.health = karmaFloor;
        } else {
            unit.damagePierce(calculatedDamage);
        }
    }
}
