package octlib.expand.entities.abilities;

import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;

public class FuelAbility extends Ability {
    public static final float resupplyInterval = 10f;

    public StatusEffect statusEffect = StatusEffects.unmoving;
    public float statusDuration = 4f;
    public float resupplyRange = 85f; // just copied from the now removed PowerAmmoType
    
    public float fuelPerItem = 120f;
    public float basePowerDrain = 50f / 60f; 
    public float powerDrain = 50f / 60f;
    public float powerCapacity;
    public Item fuelItem = Items.coal;

    protected float resupplyTimer;

    public FuelAbility(float powerCapacity) {
        this.powerCapacity = powerCapacity;
    }

    @Override
    public void addStats(Table t) {
        super.addStats(t);
        t.add(abilityStat("powercapacity", powerCapacity));
        t.row();
        t.add(abilityStat("powerdrain", basePowerDrain * 60f));
        t.row();
        t.add(fuelItem.localizedName);
    }

    @Override
    public void displayBars(Unit unit, Table bars) {
        bars.add(new Bar(() -> fuelItem.localizedName, () -> fuelItem.color, () -> data / powerCapacity)).row();
    }

    @Override
    public void created(Unit unit) {
        data = powerCapacity;
    }

    @Override
    public void update(Unit unit) {
        if ((resupplyTimer += Time.delta) >= resupplyInterval) {
            resupplyTimer = 0f;
            
            if (data < powerCapacity) {
                Building build = Units.closestBuilding(unit.team, unit.x, unit.y, resupplyRange + unit.hitSize, 
                    u -> u.block.hasItems && u.items != null && u.items.has(fuelItem));
                
                if (build != null) {
                    float fuelNeeded = powerCapacity - data;
                    
                    int maxItemsNeeded = (int) Math.ceil(fuelNeeded / fuelPerItem);
                    
                    int itemsToTake = Math.min(maxItemsNeeded, build.items.get(fuelItem));
                    
                    if (itemsToTake > 0) {
                        build.items.remove(fuelItem, itemsToTake);
                        data = Math.min(powerCapacity, data + (itemsToTake * fuelPerItem));
                        
                        Fx.itemTransfer.at(build.x, build.y, itemsToTake, fuelItem.color, unit);
                    }
                }
            }
        }


        float currentDrain = basePowerDrain + (unit.vel.len2() * 0.5f); 
        
        powerDrain = currentDrain; 

        data = Mathf.maxZero(data - (currentDrain * Time.delta));

        if (data <= 0f) {
            unit.apply(statusEffect, statusDuration);
        }
    }
}
