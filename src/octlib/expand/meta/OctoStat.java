package octlib.expand.meta;

import mindustry.gen.*;
import mindustry.world.meta.*;

public class OctoStat {
    public static final Stat
    inputEdge = new Stat("input-edge", StatCat.crafting),
    inputCorner = new Stat("input-corner", StatCat.crafting),
    healPercent = new Stat("heal-percent", StatCat.general),
    healAmount = new Stat("heal-amount", StatCat.general),
    produceChance = new Stat("produce-chance", StatCat.crafting),
    reloadFrom = new Stat("reload-from", StatCat.function),
    reloadTo = new Stat("reload-from", StatCat.function),
    recipe = new Stat("mc-recipe", StatCat.crafting),
    turretMode = new Stat("turret-modes", StatCat.function),
    KR = new Stat("kr-damage", StatCat.function),
    percentDamage = new Stat("percent-damage", StatCat.function);
}
