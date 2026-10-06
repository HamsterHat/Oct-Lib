package octlib.expand.entities.parts;

import mindustry.entities.part.DrawPart;

public abstract class AdvandedPart extends DrawPart {
    public static final AdvandedPart.AdvancedParams advParams = new AdvandedPart.AdvancedParams();

    public static class AdvancedParams extends DrawPart.PartParams{
        public float temp;

        public AdvancedParams set(float warmup, float reload, float smoothReload, float heat, float recoil, float charge, float x, float y, float rotation, float temp){
            this.warmup = warmup;
            this.reload = reload;
            this.heat = heat;
            this.recoil = recoil;
            this.smoothReload = smoothReload;
            this.charge = charge;
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.sideOverride = -1;
            this.life = 0f;
            this.sideMultiplier = 1;
            this.temp = temp;
            return this;
        }

        public AdvancedParams setRecoil(float recoils){
            this.recoil = recoils;
            return this;
        }

    }

    public interface AdvPartProg {
        AdvPartProg
                temp = p -> p.temp;

        DrawPart.PartProgress
                temperature = p -> advParams.temp;

        float get(AdvandedPart.AdvancedParams p);
    }
}