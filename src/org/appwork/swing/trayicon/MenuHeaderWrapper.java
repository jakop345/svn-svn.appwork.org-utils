package org.appwork.swing.trayicon;

import org.appwork.swing.MigPanel;

public class MenuHeaderWrapper extends MigPanel {

    public MenuHeaderWrapper(MenuHeader menuHeader) {
        super("ins 0 0 1 0,wrap 1", "[grow,fill]", "[][]");
        this.add(menuHeader);

    }

}
