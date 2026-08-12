package octlib;

import arc.Events;
import octlib.expand.blocks.energy.*;
import octlib.expand.entities.bullets.*;
import octlib.expand.entities.units.UnderwaterUnitType;
import mindustry.game.EventType;
import mindustry.mod.*;
import octlib.expand.ui.*;
import octlib.expand.blocks.campaign.RocketControlCenter;
import octlib.expand.blocks.campaign.RocketLaunchPad;

import static arc.Core.app;

public class OctLib extends Mod{
    public static RocketLaunchDialog rocketLaunch;

    public OctLib(){
        /*Events.on(EventType.FileTreeInitEvent.class, e ->
                app.post(OctLibShaders::load)
        );*/
        ClassMap.classes.put("TempGenerator", TempGenerator.class);
        ClassMap.classes.put("BlackHoleBulletType", BlackHoleBulletType.class);
        ClassMap.classes.put("AdvancedArtilleryBulletType", AdvancedArtilleryBulletType.class);
        ClassMap.classes.put("QuantumReactor", QuantumReactor.class);
        ClassMap.classes.put("UnderwaterUnitType", UnderwaterUnitType.class);
        ClassMap.classes.put("RocketControlCenter", RocketControlCenter.class);
        ClassMap.classes.put("RocketLaunchPad", RocketLaunchPad.class);
    }

    /*@Override
    public void init(){
        super.init();
        CustomStyles.load();
    }

    @Override
    public void loadContent(){
        OctLibLogic.init();
    }*/
}
