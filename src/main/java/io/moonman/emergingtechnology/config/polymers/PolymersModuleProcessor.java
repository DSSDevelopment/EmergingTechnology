package io.moonman.emergingtechnology.config.polymers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;

public class PolymersModuleProcessor {

    @Name("Disable Machine")
    @LangKey("config.emergingtechnology.common.disable.title")
    public boolean disabled = false;

    @Name("Processor Operation Energy Cost")
    @Config.Comment("How much energy the Processor uses when processing per cycle (~10 ticks).")
    @RangeInt(min = 0, max = 1000)
    public int processorEnergyBaseUsage = 50;

    @Name("Processor Operation Water Cost")
    @Config.Comment("How much water the Processor uses when processing per cycle (~10 ticks).")
    @RangeInt(min = 0, max = 1000)
    public int processorWaterBaseUsage = 50;

    @Name("Processor Operation Time")
    @Config.Comment("How long the Processor takes to process items.")
    @RangeInt(min = 0, max = 100)
    public int processorBaseTimeTaken = 20;
}