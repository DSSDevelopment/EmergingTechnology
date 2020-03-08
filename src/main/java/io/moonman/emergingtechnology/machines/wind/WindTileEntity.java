package io.moonman.emergingtechnology.machines.wind;

import com.google.common.collect.ImmutableMap;

import io.moonman.emergingtechnology.EmergingTechnology;
import io.moonman.emergingtechnology.config.EmergingTechnologyConfig;
import io.moonman.emergingtechnology.handlers.AutomationItemStackHandler;
import io.moonman.emergingtechnology.handlers.energy.EnergyStorageHandler;
import io.moonman.emergingtechnology.handlers.energy.GeneratorEnergyStorageHandler;
import io.moonman.emergingtechnology.helpers.EnergyNetworkHelper;
import io.moonman.emergingtechnology.helpers.machines.WindHelper;
import io.moonman.emergingtechnology.helpers.machines.enums.TurbineSpeedEnum;
import io.moonman.emergingtechnology.init.Reference;
import io.moonman.emergingtechnology.item.polymers.Turbine;
import io.moonman.emergingtechnology.machines.MachineTileBase;
import io.moonman.emergingtechnology.network.PacketHandler;
import io.moonman.emergingtechnology.network.animation.WindGeneratorAnimationPacket;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")
public class WindTileEntity extends MachineTileBase implements SimpleComponent {

    private final IAnimationStateMachine asm;
    private Boolean hasTurbine = false;

    public WindTileEntity() {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            asm = ModelLoaderRegistry.loadASM(new ResourceLocation(EmergingTechnology.MODID, "asms/block/wind.json"),
                    ImmutableMap.of());
        } else
            asm = null;
    }

    public ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack itemStack) {
            return true;
        }
    };

    public ItemStackHandler automationItemHandler = new AutomationItemStackHandler(itemHandler, 0, 0) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack itemStack) {
            return true;
        }
    };

    public EnergyStorageHandler energyHandler = new EnergyStorageHandler(Reference.WIND_ENERGY_CAPACITY) {
        @Override
        public void onContentsChanged() {
            markDirty();
            super.onContentsChanged();
        }
    };

    public GeneratorEnergyStorageHandler generatorEnergyHandler = new GeneratorEnergyStorageHandler(energyHandler) {

    };

    private int energy = 0;
    private TurbineSpeedEnum speed = TurbineSpeedEnum.OFF;

    @Override
    public boolean isEnergyGeneratorTile() {
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return true;
        if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            return true;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(this.generatorEnergyHandler);
        if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            return CapabilityAnimation.ANIMATION_CAPABILITY.cast(asm);
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.automationItemHandler);
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.itemHandler.deserializeNBT(compound.getCompoundTag("Inventory"));
        this.energyHandler.readFromNBT(compound);

        this.setEnergy(compound.getInteger("GuiEnergy"));

        this.setTurbineState(TurbineSpeedEnum.getById(compound.getInteger("Speed")));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", this.itemHandler.serializeNBT());
        compound.setInteger("GuiEnergy", this.getEnergy());
        compound.setInteger("Speed", TurbineSpeedEnum.getId(this.speed));
        this.energyHandler.writeToNBT(compound);

        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void cycle() {
        generate();
        spreadEnergy();
    }

    private @Nullable
    Turbine getTurbine() {
        ItemStack inventory = this.itemHandler.getStackInSlot(0);
        if (inventory.getItem() instanceof Turbine) {
            return (Turbine)inventory.getItem();
        }
        return null;
    }

    private void addDamageToTurbine() {
        ItemStack inventory = this.itemHandler.getStackInSlot(0);
        if (inventory.getItem() instanceof Turbine) {

        }
    }

    public void generate() {
        Turbine turbine;
        ItemStack inventory = this.itemHandler.getStackInSlot(0);
        if (inventory.getItem() instanceof Turbine) {
            turbine = (Turbine)inventory.getItem();
            if (inventory.getItemDamage() < inventory.getMaxDamage()) {
                this.setHasTurbine(true);
            } else {
                this.setHasTurbine(false);
                inventory.shrink(1);
            }
        } else {
            this.setHasTurbine(false);
        }
        if (this.hasTurbine && WindHelper.isGeneratorInAir(getWorld(), getPos())) {
            int energy = EmergingTechnologyConfig.ELECTRICS_MODULE.WIND.energyGenerated;

            if (WindHelper.isGeneratorAtOptimalHeight(getPos()) || getWorld().isThundering()) {
                energy *= 2;
                this.setTurbineState(TurbineSpeedEnum.FAST);
                inventory.setItemDamage(inventory.getItemDamage() + 2);
            } else {
                this.setTurbineState(TurbineSpeedEnum.SLOW);
                inventory.setItemDamage(inventory.getItemDamage() + 1);
            }

            this.energyHandler.receiveEnergy(energy, false);
        } else {
            this.setTurbineState(TurbineSpeedEnum.OFF);
        }
    }

    private void setHasTurbine(Boolean hasTurbine) {
        Boolean previousTurbineState = this.hasTurbine;
        this.hasTurbine = hasTurbine;
        if (world != null && previousTurbineState != hasTurbine) {
            world.markBlockRangeForRenderUpdate(pos, pos);
            IBlockState state = world.getBlockState(getPos());
            world.notifyBlockUpdate(pos, state, state, 3);
            world.scheduleBlockUpdate(pos, this.getBlockType(),0,0);
            markDirty();
        }
    }

    public Boolean getHasTurbine() {
        return this.hasTurbine;
    }

    private void spreadEnergy() {
        EnergyNetworkHelper.pushEnergy(getWorld(), getPos(), this.generatorEnergyHandler);
    }

    @SideOnly(Side.CLIENT)
    public void setTurbineStateClient(TurbineSpeedEnum speed) {

        String state = this.asm.currentState();
        String newState = WindHelper.getTurbineStateFromSpeedEnum(speed);

        if (!state.equalsIgnoreCase(newState)) {
            this.speed = speed;
            this.hasTurbine = speed != TurbineSpeedEnum.OFF;
            this.asm.transition(newState);
        }
    }

    private void setTurbineState(TurbineSpeedEnum speed) {

        if (speed != this.speed) {

            TargetPoint targetPoint = PacketHandler.getTargetPoint(getWorld(), getPos());

            if (targetPoint == null) return;

            PacketHandler.INSTANCE.sendToAllTracking(new WindGeneratorAnimationPacket(this.getPos(), speed),
                    targetPoint);
        }

        this.speed = speed;
    }

    // Getters

    public int getEnergy() {
        return this.energyHandler.getEnergyStored();
    }

    public TurbineSpeedEnum getTurbineState() {
        return this.speed;
    }

    // Setters

    private void setEnergy(int quantity) {
        this.energy = quantity;
    }


    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) != this ? false
                : player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
                        (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public int getField(int id) {
        switch (id) {
            case 0:
                return this.getEnergy();
            default:
                return 0;
        }
    }

    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.setEnergy(value);
                break;

        }
    }

    public ItemStack getInputStack() {
        return itemHandler.getStackInSlot(0);
    }

    public ItemStack getOutputStack() {
        return itemHandler.getStackInSlot(0);
    }

    // OpenComputers

    @Optional.Method(modid = "opencomputers")
    @Override
    public String getComponentName() {
        return "etech_wind_generator";
    }
}