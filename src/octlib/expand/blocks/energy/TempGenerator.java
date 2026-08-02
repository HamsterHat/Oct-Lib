package octlib.expand.blocks.energy;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.power.*;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class TempGenerator extends PowerGenerator {
    public float startingGeneration = 10f;
    public float decreaseAmount = 0.01f;

    public TempGenerator(String name) {
        super(name);
        solid = true;
        update = true;
        hasPower = true;
    }

    public class TempGeneratorBuild extends GeneratorBuild {
        public float currentGeneration = -1f;

        @Override
        public void updateTile() {
            if (currentGeneration < 0) {
                currentGeneration = startingGeneration;
            }

            if (currentGeneration > 0) {
                currentGeneration = Math.max(0, currentGeneration - decreaseAmount * Time.delta);
            }

            productionEfficiency = currentGeneration / startingGeneration;
            super.updateTile();
        }

        @Override
        public float getPowerProduction() {
            return enabled && currentGeneration > 0 ? currentGeneration * efficiency : 0f;
        }

        @Override
        public byte version() {
            return 2;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(currentGeneration);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 2) {
                currentGeneration = read.f();
            }
        }
    }
}
