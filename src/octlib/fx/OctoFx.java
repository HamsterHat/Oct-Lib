package octlib.fx;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import octlib.expand.blocks.campaign.*;
import octlib.expand.blocks.campaign.RocketControlCenter.*;
import octlib.expand.blocks.campaign.RocketLaunchPad.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import static arc.graphics.g2d.Draw.*;


//Code by Nullotte
public class OctoFx {
    public static Effect
    rocketMerge = new Effect(60f, e -> {
        if (!(e.data instanceof RocketControlCenterBuild build)) return;
        alpha(e.fout());
        mixcol(Pal.accent, 1f);
        rect(((RocketControlCenter) build.block).rocketRegion, e.x, e.y);
    }),
    countdownNumber = new Effect(50f, e -> {
        if (!(e.data instanceof RocketControlCenterBuild build)) return;
        color(build.team.color, e.foutpowdown());
        rect(((RocketControlCenter) build.block).countdownNumberRegions[(int) e.rotation], e.x, e.y);
    });
}
