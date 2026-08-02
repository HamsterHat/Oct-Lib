package octlib.expand.blocks.energy;


import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.content.Fx;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.power.*;
import octlib.*;

public class QuantumReactor extends ImpactReactor {
    public float explosionRadius = 320f;
    public float explosionDamage = 25000f;
    public float attractStrength = 14f;
    public float maxPullRadius = 480f;
    public float anomalyLifetime = 300f;
    
    public Color coreColor = Color.valueOf("000000");
    public Color energyColor = Color.valueOf("fffffe");
    public Color plasmaColor = Color.valueOf("a9fcfb");
    
    public Effect anomalyDespawnEffect = Fx.none;
    public Effect anomalyUpdateEffect = Fx.none;
    public Effect anomalyMiniExplodeEffect = Fx.none;
    public Effect shockwaveHitEffect = Fx.none;
    
    public Sound anomalyAmbientSound = Sounds.none;
    public Sound anomalyUpdateSound = Sounds.none;
    public Sound anomalyMiniExplodeSound = Sounds.none;
    public Sound anomalyDespawnSound = Sounds.none;
    public Sound shockwaveHitSound = Sounds.none;
    
    public float miniExplodeInterval = 12f;
    public float updateVfxInterval = 8f;
    public int shockwaveCount = 16;
    public float shockwaveDamage = 1200f;

    public @Nullable BulletType collapseAnomaly;

    public QuantumReactor(String name){
        super(name);
    }

    @Override
    public void init(){
        if(collapseAnomaly == null){
            collapseAnomaly = new BulletType(0f, 0f) {
                {
                    absorbable = hittable = false;
                    lifetime = anomalyLifetime;
                    hitColor = lightColor = lightningColor = energyColor;
                    despawnEffect = Fx.none;
                    hitShake = despawnShake = 60f;
                }

                @Override
                public void update(Bullet b){
                    super.update(b);
                    float progress = b.fin();
                    
                    Effect.shake(6f * progress, 12f * progress, b.x, b.y);

                    if(anomalyAmbientSound != null && b.timer(3, 30f)){
                        anomalyAmbientSound.at(b.x, b.y, Mathf.random(0.9f, 1.1f));
                    }

                    Units.nearby(Tmp.r1.setCenter(b.x, b.y).setSize(maxPullRadius * 2f), u -> {
                        if(u.team == b.team) return;
                        float dst = u.dst(b.x, b.y);
                        if(dst < maxPullRadius){
                            float force = (1f - (dst / maxPullRadius)) * attractStrength * Time.delta * 8f;
                            u.impulse(Tmp.v1.set(u).sub(b.x, b.y).nor().scl(-force * u.mass()));
                            if(dst < 40f && b.timer(2, 10f)){
                                u.damage(450f);
                            }
                        }
                    });

                    Groups.bullet.intersect(b.x - maxPullRadius, b.y - maxPullRadius, maxPullRadius * 2f, maxPullRadius * 2f, other -> {
                        if(other != b && other.team != b.team && Mathf.dst(b.x, b.y, other.x, other.y) <= maxPullRadius){
                            other.vel.add(Tmp.v1.set(b.x - other.x, b.y - other.y).setLength(2.5f * Time.delta));
                            if(Mathf.dst(b.x, b.y, other.x, other.y) <= 30f){
                                other.remove();
                            }
                        }
                    });

                    if(b.timer(0, updateVfxInterval)){
                        if(anomalyUpdateEffect != null){
                            for(int i = 0; i < 3; i++){
                                float angle = Mathf.random(360f);
                                anomalyUpdateEffect.at(b.x, b.y, angle, plasmaColor);
                            }
                        }
                        if(anomalyUpdateSound != null){
                            anomalyUpdateSound.at(b.x, b.y, Mathf.random(0.8f, 1.2f));
                        }
                    }

                    if(b.timer(1, miniExplodeInterval)){
                        float rangeOffset = explosionRadius * progress * 0.8f;
                        float rx = b.x + Mathf.range(rangeOffset);
                        float ry = b.y + Mathf.range(rangeOffset);
                        if(anomalyMiniExplodeEffect != null) anomalyMiniExplodeEffect.at(rx, ry, energyColor);
                        if(anomalyMiniExplodeSound != null) anomalyMiniExplodeSound.at(rx, ry, Mathf.random(0.9f, 1.1f));
                        Damage.damage(b.team, rx, ry, 80f * progress, 800f * progress);
                    }
                }

                @Override
                public void draw(Bullet b){
                    float f = b.fin();
                    float fout = b.fout();
                    float baseRad = 24f + (explosionRadius * 0.15f) * f;

                    Draw.z(Layer.effect + 2f);

                    Draw.color(plasmaColor);
                    Draw.alpha(0.15f * fout);
                    Fill.circle(b.x, b.y, maxPullRadius * f * (1f + Mathf.absin(Time.time, 6f, 0.05f)));

                    Draw.color(energyColor);
                    Draw.alpha(0.4f * f);
                    Lines.stroke(4f * fout);
                    Lines.circle(b.x, b.y, baseRad * 1.3f);

                    for(int i = 0; i < 4; i++){
                        float triAngle = (i * 90f) + (b.time * 2.5f);
                        float triW = 8f * fout;
                        float triL = (explosionRadius * 0.6f) * f * (1f + Mathf.sin(Time.time + i * 20, 8f, 0.1f));
                        Drawf.tri(b.x, b.y, triW, triL, triAngle);
                        Drawf.tri(b.x, b.y, triW, triL * 0.5f, triAngle + 180f);
                    }

                    Draw.color(coreColor);
                    Fill.circle(b.x, b.y, baseRad * fout);
                    Fill.circle(b.x, b.y, baseRad * 0.7f * fout);

                    Draw.reset();
                }

                @Override
                public void despawned(Bullet b){
                    super.despawned(b);
                    
                    if(anomalyDespawnSound != null) anomalyDespawnSound.at(b.x, b.y);

                    if(anomalyDespawnEffect != null) anomalyDespawnEffect.at(b.x, b.y, energyColor);

                    if(shockwaveCount > 0){
                        float angleStep = 360f / shockwaveCount;
                        for(int i = 0; i < shockwaveCount; i++){
                            float angle = (i * angleStep) + Mathf.random(10f);
                            BulletType shockwave = new BulletType(3f, shockwaveDamage){
                                { lifetime = 40f; absorbable = hittable = false; despawnEffect = Fx.none; hitEffect = shockwaveHitEffect; hitSound = shockwaveHitSound; }
                            };
                            shockwave.create(b, b.team, b.x, b.y, angle, 1f, 1f);
                        }
                    }

                    Damage.dynamicExplosion(b.x, b.y, 140f, 90f, 100f, 160f, true, true, null, anomalyDespawnEffect, 45f);
                    HppUtilities.absoluteDamage(b.x, b.y, 150f, 999999f);
                }
            };
        }
        super.init();
    }

    public class QuantumReactorBuild extends ImpactReactorBuild {
        public void createExplosion(){
            if(collapseAnomaly != null){
                collapseAnomaly.create(this, team, x, y, 0f, 1f, 1f);
            }
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            if(Vars.state.rules.reactorExplosions && warmup > 0.3f){
                createExplosion();
            }
        }
    }
}
