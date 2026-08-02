package octlib;

import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Posc;

import static java.lang.Math.cos;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class HppUtilities {
    private static final IntSet collidedBlocks = new IntSet();

    public static void blackHoleUpdate(Team t, Posc owner, float damageRadius, float pullStrength, float damage, float armorMultiplier, float damageMultiplier, float buildingDamageMp){
        float x = owner.x(), y = owner.y();
        completeDamage(t,owner.x(), owner.y(), damageRadius, damage * damageMultiplier, buildingDamageMp, armorMultiplier == -1, armorMultiplier);
        Units.nearbyEnemies(t, owner.x() - damageRadius, owner.y() - damageRadius, damageRadius * 2, damageRadius * 2, u ->{
            float pullingRadius = damageRadius + u.hitSize / 2;
            if(!u.type.internal && u.hittable() && u.within(x, y, pullingRadius) && owner != u){
                Vec2 impulse = Tmp.v1.trns(u.angleTo(x, y), pullStrength + (1f - u.dst(x, y) / pullingRadius) * pullStrength);
                u.impulseNet(impulse);
            }
        });
    }

    public static void completeDamage(Team team, float x, float y, float radius, float damage, float buildingDamageMultiplier, boolean pierceArmor, float armorMultiplier){
        Units.nearbyEnemies(team, x - radius, y - radius, radius * 2f, radius * 2f, unit -> {
            if(!unit.dead && unit.hittable() && unit.within(x, y, radius + unit.hitSize / 2f)){
                if(pierceArmor){
                    unit.damagePierce(damage * (1f - unit.dst(x, y) / (radius + unit.hitSize / 2)));
                }else if(armorMultiplier == 1){
                    unit.damage(damage * (1f - unit.dst(x, y) / (radius + unit.hitSize / 2)));
                } else unit.damageArmorMult(damage * (1f - unit.dst(x, y) / (radius + unit.hitSize / 2)), armorMultiplier);
            }
        });

        trueEachBlock(x, y, radius, build -> {
            if(build.team != team && !build.dead && build.block != null){
                if(pierceArmor){
                    build.damagePierce(damage * buildingDamageMultiplier * (1f - build.dst(x, y) / radius));
                }else if(armorMultiplier != 1){
                    build.damage(damage * buildingDamageMultiplier * (1f - build.dst(x, y) / radius));
                } else build.damageArmorMult(damage * buildingDamageMultiplier, armorMultiplier * (1f - build.dst(x, y) / radius));
            }
        });
    }

    public static void trueEachBlock(float wx, float wy, float range, Cons<Building> cons){
        collidedBlocks.clear();
        int tx = World.toTile(wx);
        int ty = World.toTile(wy);

        int tileRange = Mathf.floorPositive(range / tilesize);

        for(int x = tx - tileRange - 2; x <= tx + tileRange + 2; x++){
            for(int y = ty - tileRange - 2; y <= ty + tileRange + 2; y++){
                if(Mathf.within(x * tilesize, y * tilesize, wx, wy, range)){
                    Building other = world.build(x, y);
                    if(other != null && !collidedBlocks.contains(other.pos())){
                        cons.get(other);
                        collidedBlocks.add(other.pos());
                    }
                }
            }
        }
    }

    public static Color lerpColor(Color color1, Color color2, float progress) {
        progress = Mathf.clamp(progress, 0f, 1f);
        return Tmp.c1.set(color1).lerp(color2, progress);
    }

    public static Color flashingColor(Color color1, Color color2, float mag){
        float pr =  Mathf.absin(Time.time, 1, mag);
        return Tmp.c1.set(color1).lerp(color2, pr);
    }

    public static void absoluteDamage(float x, float y, float radius, float damage) {
        Units.nearby(x - radius, y - radius, radius * 2f, radius * 2f, unit -> {
            float maxDst = radius + unit.hitSize / 2f;
            if (!unit.dead && unit.hittable() && unit.within(x, y, maxDst)) {
                float falloff = 1f - (unit.dst(x, y) / maxDst);
                unit.damage(damage * falloff);
            }
        });


        int tx = World.toTile(x);
        int ty = World.toTile(y);
        int tileRange = (int) (radius / tilesize) + 1;
        float rangeSq = radius * radius;

        for (int txX = tx - tileRange; txX <= tx + tileRange; txX++) {
            for (int tyY = ty - tileRange; tyY <= ty + tileRange; tyY++) {
                if (Mathf.dst2(txX * tilesize, tyY * tilesize, x, y) <= rangeSq) {
                    Building build = world.build(txX, tyY);
                    if (build != null && !build.dead && build.block != null) {
                        float falloff = 1f - (build.dst(x, y) / radius);
                        build.damage(damage * falloff);
                    }
                }
            }
        }
    }

}
