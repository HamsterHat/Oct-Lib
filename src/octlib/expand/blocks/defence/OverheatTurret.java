package octlib.expand.blocks.defence;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import octlib.expand.draw.DrawOverheatTurret;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.consumers.ConsumeLiquidFilter;

import static octlib.HppUtilities.flashingColor;
import static octlib.HppUtilities.lerpColor;
import static mindustry.Vars.tilesize;

public class OverheatTurret extends ItemTurret{
    //Base temperature
    public float startTemperature = 24f;
    //Maximum overheating threshold temperature
    public float overheatTemperature = 200;
    //It is needed if you use both mechanics: melting and fully shutting down the turret.
    public float meltingTemperature = overheatTemperature;
    //Minimum temperature threshold for cooling down
    public float cooldownThreshold = 70;
    //The number of degrees the temperature rises per shot
    public float temperaturePerShot = 40;
    //Cooling down speed, when turret is inactive. (Example: 0.5f = 30°/sec.)
    public float cooldownSpeed = 0.85f;
    //Cooling down speed, when turret is overheated. (Example: 0.5f = 30°/sec.)
    public float overheatCooldownSpeed = 0.5f;
    //If true turret shuts down before cooling.
    public boolean cooldown = false;
    //If true turret takes damage when temperature higher than meltingTemperature
    public boolean melting = true;

    public float shootPenalty = 0;
    //Per tick
    public float meltingDamage = 0.25f;
    //The portion of health that remains even upon overheating
    public float meltingThreshold = 0.2f;
    //Turret cooling efficiency relative to its acceleration
    public float coolantCooldownEfficiency = 1;
    //Efficiency depends on coolantEfficiency.
    public boolean coolantEfficiencyDepends = false;


    public OverheatTurret(String name){
        super(name);
        drawer = new DrawOverheatTurret();
    }

    @Override
    public void setBars(){
        super.setBars();
        Color color= Pal.redDust;
        addBar("temperature", (OverheatTurret.OverHeatTurretBuild entity) ->
                new Bar(
                        () -> Core.bundle.format("bar.temperature", Strings.autoFixed(entity.temperature, 0)) + '/' + (cooldown ? overheatTemperature : meltingTemperature) + '°' ,
                        () -> !(melting) ? lerpColor(Pal.redDust, Color.valueOf("fc4242"), entity.temperature / overheatTemperature) : (entity.temperature < meltingTemperature) ? lerpColor(Pal.redDust, Color.valueOf("fc4242"), entity.temperature / meltingTemperature) : flashingColor(Pal.redDust, Color.valueOf("d60000"), 3),
                        () -> entity.temperature / (cooldown ? overheatTemperature : meltingTemperature)
                )
        );
    }

    public class OverHeatTurretBuild extends ItemTurretBuild{
        public float temperature = startTemperature;
        public boolean overheated = false;
        public float penalty;

        @Override
        public void update(){
            updateOverheat();
            if(!overheated || !cooldown) {
                super.update();
                return;
            }
            unit.tile(this);
            unit.rotation(rotation);
            unit.team(team);
            curRecoil = Mathf.approachDelta(curRecoil, 0, 1 / recoilTime);
            recoilOffset.trns(rotation, -Mathf.pow(curRecoil, recoilPow) * recoil);
            reloadCounter = Mathf.lerpDelta(reloadCounter, 0, 0.1f);
            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }
            updateCooldown();
        }

        public void updateOverheat(){
            penalty = Mathf.approachDelta(penalty, 0, 1);
            if(temperature >= meltingTemperature & melting){
                this.damage(meltingDamage);
            }
            overheated = overheated || temperature >= overheatTemperature & cooldown;
            temperature = overheated || penalty > 0 ? temperature : Mathf.approachDelta(temperature, startTemperature, cooldownSpeed);
        }

        public void updateCooldown(){
            updateOverheatCooling();
            temperature = Mathf.approachDelta(temperature, startTemperature, overheatCooldownSpeed);
            if(temperature <= cooldownThreshold) overheated = false;
        }

        public void updateOverheatCooling(){
            if(coolant != null && coolant.efficiency(this) > 0){
                float capacity = coolant instanceof ConsumeLiquidFilter filter ? filter.getConsumed(this).heatCapacity : (coolant.consumes(liquids.current()) ? liquids.current().heatCapacity : 0.4f);
                float amount = coolant.amount * coolant.efficiency(this);
                coolant.update(this);
                temperature = Mathf.approachDelta(temperature, startTemperature, amount * capacity * (coolantEfficiencyDepends ? coolantMultiplier * coolantCooldownEfficiency : coolantCooldownEfficiency) );
                if(Mathf.chance(0.15 * amount)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }
            }
        }

        
        protected void updateCooling(){
            if(coolant != null && coolant.efficiency(this) > 0 && efficiency > 0){
                float capacity = coolant instanceof ConsumeLiquidFilter filter ? filter.getConsumed(this).heatCapacity : (coolant.consumes(liquids.current()) ? liquids.current().heatCapacity : 0.4f);
                float amount = coolant.amount * coolant.efficiency(this);
                coolant.update(this);
                temperature = penalty >0 ? temperature : Mathf.approachDelta(temperature, startTemperature, amount * capacity * (coolantEfficiencyDepends ? coolantMultiplier * coolantCooldownEfficiency : coolantCooldownEfficiency));
                reloadCounter += amount * edelta() * capacity * coolantMultiplier * ammoReloadMultiplier();

                if(Mathf.chance(0.06 * amount)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }
            }
        }

        @Override
        public void draw(){

        }

        @Override
        public void shoot(BulletType type){
            super.shoot(type);
            temperature += temperaturePerShot;
            penalty = shootPenalty;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(temperature);
            write.f(penalty);
            write.bool(overheated);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            temperature = read.f();
            penalty = read.f();
            overheated = read.bool();
        }
    }
}