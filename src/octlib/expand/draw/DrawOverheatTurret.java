package octlib.expand.draw;

import arc.graphics.g2d.Draw;
import hjsonpp.expand.blocks.defense.OverheatTurret;
import octlib.expand.entities.parts.*;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.draw.DrawTurret;

public class DrawOverheatTurret extends DrawTurret {

    @Override
    public void draw(Building build){
        OverheatTurret turret = (OverheatTurret)build.block;

        OverheatTurret.OverHeatTurretBuild tb = (OverheatTurret.OverHeatTurretBuild)build;

        Draw.rect(base, build.x, build.y);
        Draw.color();

        Draw.z(shadowLayer);

        Drawf.shadow(preview, build.x + tb.recoilOffset.x - turret.elevation, build.y + tb.recoilOffset.y - turret.elevation, tb.drawrot());

        Draw.z(turretLayer);

        drawTurret(turret, tb);
        drawHeat(turret, tb);

        if(parts.size > 0){
            if(outline.found()){
                //draw outline under everything when parts are involved
                Draw.z(turretLayer - 0.01f);
                Draw.rect(outline, build.x + tb.recoilOffset.x, build.y + tb.recoilOffset.y, tb.drawrot());
                Draw.z(turretLayer);
            }

            float progress = tb.progress();

            //TODO no smooth reload
            var params = AdvandedPart.advParams.set(build.warmup(), 1f - progress, 1f - progress, tb.heat, tb.curRecoil, tb.charge, tb.x + tb.recoilOffset.x, tb.y + tb.recoilOffset.y, tb.rotation, tb.temperature / turret.overheatTemperature);

            for(var part : parts){
                params.setRecoil(part.recoilIndex >= 0 && tb.curRecoils != null ? tb.curRecoils[part.recoilIndex] : tb.curRecoil);
                part.draw(params);
            }
        }
        if(ammoParts.size > 0 && tb.getAmmoContent() != null){
            float progress = tb.progress();
            var params = AdvandedPart.advParams.set(build.warmup(), 1f - progress, 1f - progress, tb.heat, tb.curRecoil, tb.charge, tb.x + tb.recoilOffset.x, tb.y + tb.recoilOffset.y, tb.rotation, tb.temperature / turret.overheatTemperature);

            var parts = ammoParts.get(tb.getAmmoContent());
            if(parts != null){
                for(var part : parts){
                    params.setRecoil(part.recoilIndex >= 0 && tb.curRecoils != null ? tb.curRecoils[part.recoilIndex] : tb.curRecoil);
                    part.draw(params);
                }
            }
        }
    }
}