package octlib.expand.blocks.effect;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import arc.audio.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.InputHandler;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.*;

import mindustry.type.Item;
import mindustry.world.blocks.distribution.Conveyor;

import mindustry.Vars;


import static mindustry.Vars.tilesize;

public class BombBlock extends Block {
    public float fuseTime = 140f;
    public boolean canClick = true;

    public TextureRegion topRegion;

    public Effect smokeEffect = Fx.none;
    public Effect fireEffect = Fx.none;
    public float smokeChance = 0.08f, fireChance = 0.12f;
    
    public Sound fuseSound = Sounds.none;

    public BombBlock(String name){
        super(name);

        configurable = true;
        saveConfig = false;

        baseExplosiveness = 0.3f;
        rebuildable = false;

        solid = true;
        sync = true;
        breakable = true;
        update = true;
        hasPower = true;

        autoResetEnabled = false;
        drawDisabled = false;
        enableDrawStatus = false;

        config(Boolean.class, (BombBuild build, Boolean b) -> {
            if(b && !build.lit) build.light();
        });
    }

    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(this.name + "-top");
    }

    @Override
    public void init() {
        super.init();
        configurable = canClick;
    }


    @Override
    public void setBars() {
        super.setBars();

        addBar("heat", (BombBlock.BombBuild entity) -> new Bar("bar.heat", Pal.lightOrange, () -> Mathf.clamp((fuseTime - entity.heat) / fuseTime)));
    }


    public class BombBuild extends Building {
        public float heat = 0f;
        public boolean lit = false;

        @Override
        public void placed(){
            super.placed();
            heat = fuseTime;
        }

        public void draw(){
            super.draw();
            if(lit && heat % 45f < 22.5f) Draw.rect(topRegion, x, y);
        }



        @Override
        public boolean configTapped(){
            if(canClick) {
                if (lit) return false;
                configure(true);
            }
            return false;
        }

        public void updateTile(){
            if(lit){
                heat -= delta();
                if(Mathf.chance(smokeChance)) smokeEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                if(fireEffect != Fx.none && Mathf.chance(fireChance)) fireEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                if(heat <= 0f) kill();
            }
            else if(canConsume() || tile.floor().attributes.get(Attribute.heat) > 0.01) light();
        }

        public void light(){
            fuseSound.at(x, y);
            heat = fuseTime;
            lit = true;
        }



        @Override
        public void write(Writes w){
            super.write(w);
            w.bool(lit);
            w.f(heat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            lit = read.bool();
            heat = read.f();
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case heat -> heat;
                case enabled -> lit ? 1 : 0;
                default -> super.sense(sensor);
            };
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.enabled){
                boolean shouldLight = !Mathf.zero(p1);

                if(Vars.net.client() || lit || !shouldLight){
                    return;
                }

                configureAny(true);
            }
        }
    }
}