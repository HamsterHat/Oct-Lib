package octlib.expand.entities.units;

import mindustry.type.UnitType;
import mindustry.graphics.Layer;

public class UnderwaterUnitType extends UnitType {

    public UnderwaterUnitType(String name) {
        super(name);
        
        this.constructor = UnderwaterUnit::new;
        
        this.flying = false;
        this.naval = true;
        this.canDrown = false;
        
        this.groundLayer = Layer.scorch;
    }
}
