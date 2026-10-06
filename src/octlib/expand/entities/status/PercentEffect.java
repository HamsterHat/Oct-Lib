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

public class PercentEffect extends StatusEffect {
    public float percentDamage = 0.01f / 60f; 
    public boolean percentPierce = false;


    public PercentEffect(String name) {
        super(name);
    }
   

    @Override
    public void setStats() {
        super.setStats();
        stats.add(OctoStat.percentDamage, percentDamage * 60f * 100f, StatUnit.perSecond);
    }

    @Override
    public void update(Unit unit, float time){
        super.update(unit, time);
        
        float calculatedDamage = unit.health * percentDamage * Time.delta;

        if (!percentPierce) {
            unit.damage(calculatedDamage);
        } else {
            unit.damagePierce(calculatedDamage);
        }
    }
}
