package octlib.expand.entities.status;

import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

public class SpreadEffect extends StatusEffect {
    public float spreadRadius = 5;
    public float spreadInterval = 60;
    public boolean spreadSingle = true;
    public Effect spreadEffect = Fx.plasticburn;
    public boolean spreadAllies = true;
    public boolean spreadEnemies = false;

    public SpreadEffect(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, float time){
        super.update(unit, time);
        
        if(time % spreadInterval < 1){
            final float nextTime = time * 0.9f;

            if(spreadSingle){
                Unit u = Units.closest(null, unit.x, unit.y, unit.hitSize + spreadRadius, un -> {
                    if(un == unit) return false;
                    
                    boolean isAlly = (un.team == unit.team);
                    if(isAlly && !spreadAllies) return false;
                    if(!isAlly && !spreadEnemies) return false;

                    return !un.isImmune(this) && !un.hasEffect(this);
                });
                
                if(u != null){
                    u.apply(this, nextTime);
                    spreadEffect.at(u.x, u.y, unit.angleTo(u.x, u.y));
                }
            }
            else {
                Units.nearby(null, unit.x, unit.y, unit.hitSize + spreadRadius, u -> {
                    if(u == unit) return;

                    boolean isAlly = (u.team == unit.team);
                    if(isAlly && !spreadAllies) return;
                    if(!isAlly && !spreadEnemies) return;

                    if (!u.isImmune(this) && !u.hasEffect(this)) {
                        u.apply(this, nextTime);
                        spreadEffect.at(u.x, u.y, unit.angleTo(u.x, u.y));
                    } else if (u.hasEffect(this)) {
                        u.damagePierce(damage * spreadInterval / 2.25f);
                    }
                });
            }
        }
    }
}
