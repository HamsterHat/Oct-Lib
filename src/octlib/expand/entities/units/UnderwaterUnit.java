package octlib.expand.entities.units;

import mindustry.gen.UnitWaterMove;
import mindustry.graphics.Drawf;

public class UnderwaterUnit extends UnitWaterMove {

    @Override
    public void draw() {
        Drawf.underwater(() -> {
            super.draw();
        });
    }
}
