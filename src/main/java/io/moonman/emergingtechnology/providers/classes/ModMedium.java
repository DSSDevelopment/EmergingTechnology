package io.moonman.emergingtechnology.providers.classes;

public class ModMedium {

    public int id;
    public String name;
    public int waterUsage;
    public int growthModifier;
    public int destroyChance;

    public int boostModifier;
    public boolean allPlants;
    public String[] plants;


    public ModMedium(int id, String name, int waterUsage, int growthModifier, String[] plants, int boostModifier, int destroyChance) {
        this.id = id;
        this.name = name;
        this.waterUsage = waterUsage;
        this.growthModifier = growthModifier;
        this.destroyChance = destroyChance;

        this.boostModifier = boostModifier;
        this.allPlants = plants.length == 0;
        this.plants = plants;
    }
}