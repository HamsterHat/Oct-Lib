package octlib.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.Angles;
import mindustry.entities.Effect;

public class OctoFx {
    
    public static final Effect purpleFire = new Effect(30f, e -> {
        Draw.color(Color.valueOf("d535d9"), Color.valueOf("8a2be2"), e.fin());
        
        Fill.circle(e.x, e.y, e.fout() * 2f + Mathf.random(1f));
        
        Angles.randLenVectors(e.id, 2, 1f + e.fin() * 10f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.2f);
        });
    });

}
