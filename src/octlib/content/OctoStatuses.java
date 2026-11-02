package octlib.content;

import arc.graphics.Color;
import mindustry.type.StatusEffect;
import mindustry.content.StatusEffects;
import mindustry.content.Fx;
import octlib.expand.entities.status.*;

import static octlib.content.OctoFx.*;

public class OctoStatuses {
    public static StatusEffect malaria, KR, KR_II, KR_III;

    public static void load() {
        malaria = new SpreadEffect("mlr") {{
            damage = 1f / 60f;
            intervalDamage = 25f;
            intervalDamageTime = 10 * 60f;
            intervalDamagePierce = true;
            outline = false;
            damageMultiplier = 0.15f;
            healthMultiplier = 0.8f;
            reloadMultiplier = 0.2f;
            speedMultiplier = 0.8f;
            spreadRadius = 5f;
            spreadInterval = 80f;
            spreadSingle = false;
            spreadEffect = Fx.none;
            spreadAllies = true;
            spreadEnemies = true;
        }};

        KR = new KarmaEffect("KR") {{
            damage = 0f;
            outline = false;
            effect = purpleFire;
        }};

        KR_II = new KarmaEffect("KR_II") {{
            damage = 0f;
            outline = false;
            effect = purpleFire;
            percentDamage = 0.2f / 60f;
        }};

        KR_III = new KarmaEffect("KR_III") {{
            damage = 0f;
            outline = false;
            effect = purpleFire;
            percentDamage = 0.3f / 60f;
        }};

    }
}
