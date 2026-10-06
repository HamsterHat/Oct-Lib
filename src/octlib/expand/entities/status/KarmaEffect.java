package octlib.expand.entities.status;

import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.entities.units.StatusEntry;
import arc.util.Time;

public class KarmaEffect extends StatusEffect {
    public float percentDamage = 0.1f / 60f; 
    public float karmaFloor = 1f; 

    public KarmaEffect(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, StatusEntry entry) {
        super.update(unit, entry);
        
        float calculatedDamage = unit.health * percentDamage * Time.delta;

        if (unit.health <= karmaFloor || (unit.health - calculatedDamage) <= karmaFloor) {
            unit.health(karmaFloor);
        } else {
            unit.damage(calculatedDamage);
        }
    }
}
