package octlib.expand.entities.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Bullet;

import static octlib.HppUtilities.*;

public class BlackHoleBulletType extends BulletType {
    public int damageInterval = 10;
    public float layer = -1;
    public float shrinkTime = 60f;
    public float fadeTime = 20;
    public float growTime = 10f;
    public float eventHorizonRadius = 48;
    public float accretionDiskWidth = 8;
    public float inclinedDiskwidth = 0.8f;
    public float offset = 7;
    public Color accretionDiskColor = Color.valueOf("fffffe");
    public boolean clampProgress = true;
    public float factorRadius = 170;
    public float pullStrength = 3;
    public float shake = 0;
    public Effect updateEffect;
    public float updateEffectChance = 0.75f;
    public float updateEffectTime = -1;

    public BlackHoleBulletType(float speed, float damage){
        super(speed, damage);
        hittable = absorbable = false;
        collides = false;
        shootEffect = smokeEffect = Fx.none;
        despawnEffect = Fx.none;
    }
    @Override
    public void init(Bullet b){
        super.init(b);
    }

    public BlackHoleBulletType(){
        this(0f, 1f);
    }

    @Override
    public float continuousDamage(){
        return damage * (60f / damageInterval);
    }

    @Override
    public void update(Bullet b){

        if(updateEffect != null && Mathf.chance(updateEffectChance)  && (updateEffectTime == -1 || b.time < updateEffectTime)) {
            updateEffect.at(b.x, b.y, b.rotation());
        }
        if(b.timer(1, damageInterval)){
            Effect.shake(shake, shake, b.x, b.y);
            blackHoleUpdate(b.team, b, factorRadius, Math.max(pullStrength, pullStrength * Time.delta), Math.max(damage, damage * Time.delta), armorMultiplier, damageMultiplier(b), buildingDamageMultiplier);
        }
    }

    @Override
    public void draw(Bullet b){
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);

        drawParts(b);
        drawTrail(b);

        float px = b.x, py = b.y;
        float prog = fout(b);
        
        float horizonRad = eventHorizonRadius * prog;
        float outerDiskRad = (eventHorizonRadius + offset + accretionDiskWidth) * prog;

        float glowScale = 1f + Mathf.absin(Time.time, 8f, 0.08f);
        Draw.color(accretionDiskColor);
        Draw.alpha(0.25f * prog);
        Fill.circle(px, py, outerDiskRad * 1.4f * glowScale);

        int segments = 32;
        float rotation = b.time * 1.5f;
        float diskBaseRad = horizonRad + offset;

        for(int i = 0; i < 3; i++){
            float layerOffset = i * (accretionDiskWidth / 3f);
            float alphaMult = 1f - (i / 3f);
            
            Draw.color(accretionDiskColor);
            Draw.alpha(alphaMult * (0.4f + Mathf.absin(Time.time + i * 10, 5f, 0.3f)) * prog);
            Lines.stroke((accretionDiskWidth / 3f) * prog);
            
            Lines.poly(px, py, segments, diskBaseRad + layerOffset, rotation * (1f + i * 0.2f));
        }

        Draw.z(Math.max(layer, 120f));
        Draw.color(Color.black);
        Fill.circle(px, py, horizonRad);
        Fill.circle(px, py, horizonRad * 0.85f);

        Draw.color(accretionDiskColor);
        Draw.alpha(0.7f * prog);
        
        float rectWidth = outerDiskRad * 2.2f;
        float rectHeight = accretionDiskWidth * 1.2f * prog;
        
        Fill.rect(px, py + (offset * 0.2f), rectWidth, rectHeight);

        Draw.alpha(0.4f * prog);
        Fill.rect(px, py + (offset * 0.2f), rectWidth * 0.8f, rectHeight * 1.5f);

        Draw.reset();
        Draw.z(z);
    }



    public float fout(Bullet b){
        return Interp.sineOut.apply(
                Mathf.curve(b.time, 0f, growTime) - Mathf.curve(b.time, b.lifetime - shrinkTime, b.lifetime)
        );
    }

    @Override
    public void drawLight(Bullet b){
        //none
    }

}