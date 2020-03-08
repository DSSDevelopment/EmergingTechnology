package io.moonman.emergingtechnology.item.polymers;

import io.moonman.emergingtechnology.item.ItemBase;

public class Turbine extends ItemBase {

    public Turbine() {
        super("turbine");
        this.maxStackSize = 1;
        this.setMaxDamage(120);
    }
}