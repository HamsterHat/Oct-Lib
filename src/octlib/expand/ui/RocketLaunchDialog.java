package octlib.expand.ui;

import arc.*;
import arc.graphics.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
//Code by Nullotte
public class RocketLaunchDialog extends BaseDialog {
    public RocketLaunchDialog() {
        super("@launch.text");
    }

    public void show(Runnable run, String dest, String desc) {
        cont.clear();
        buttons.clear();

        buttons.defaults().size(210f, 64f);
        buttons.button("@back", Icon.left, this::hide);
        addCloseListener();

        cont.table(t -> {
            t.table(title -> {
                title.add(dest);
            });
            t.row();
            t.add(desc).width(720f).pad(30f).wrap().labelAlign(Align.center);
        });

        cont.row();

        buttons.button("@launch.text", Icon.play, () -> {
            run.run();
            hide();
        });

        show();
    }
}
