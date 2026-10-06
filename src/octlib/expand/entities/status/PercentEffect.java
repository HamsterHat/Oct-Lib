package octlib.expand.entities.status;

import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.entities.units.StatusEntry;
import arc.util.Time;

public class PercentEffect extends StatusEffect {
    public float percentDamage = 0.01f / 60f; 
    public boolean percentPierce = false;

    public PercentEffect(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, StatusEntry entry) {
        super.update(unit, entry);
        
        float calculatedDamage = unit.health * percentDamage * Time.delta;

        if (!percentPierce) {
            unit.damage(calculatedDamage);
        } else {
            unit.damagePierce(calculatedDamage);
        }
    }
}
