package io.moonman.emergingtechnology.providers.classes;

public class ModTissue {

    public String displayName;
    public String registryName;
    public String entityId;
    public String result;


    public ModTissue(String displayName, String entityId, String result) {
        this.displayName = displayName;
        this.entityId = entityId;
        this.result = result;
        this.registryName = this.entityId.replace(":", "_");
    }
}