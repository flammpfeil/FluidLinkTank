package mods.flammpfeil.fluidlinktank;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Furia on 14/10/20.
 */
public class StorageLinkTank extends WorldSavedData{


    public static String sanitizeFilename(String name) {
        return name.replaceAll("[:\\\\/*?|<>]", "_");
    }
    static public StorageLinkTank getLinkFluidStorage(String key){
        if(key == null) return null;
        if(key.trim().length() == 0) return null;

        MinecraftServer server = MinecraftServer.getServer();
        if(server == null) return null;
        if(server.worldServers.length == 0) return null;
        World world = server.getEntityWorld();
        if(world.isRemote) return null;

        if(world == null) return null;

        String name = "flampfeil.lfc_"+sanitizeFilename(key);

        StorageLinkTank result = (StorageLinkTank)world.loadItemData(StorageLinkTank.class,name);
        if(result == null){
            world.setItemData(name,new StorageLinkTank(name));

            result = (StorageLinkTank)world.loadItemData(StorageLinkTank.class,name);
        }

        return result;
    }

    static public StorageLinkTank getLinkFluidStorage(Fluid fluid){
        if(fluid == null) return null;

        String key = "fluid_" + fluid.getName();

        return getLinkFluidStorage(key);
    }

    protected FluidTank tank;

    public FluidTank getTank(){
        return this.tank;
    }

    public StorageLinkTank(String par1Str) {
        super(par1Str);

        this.tank = new FluidTankStorageWrapper(this, FluidLinkTank.TankMaxAmount);
    }

    @Override
    public void readFromNBT(NBTTagCompound var1) {
        this.tank.readFromNBT(var1);

    }

    @Override
    public void writeToNBT(NBTTagCompound var1) {
        this.tank.writeToNBT(var1);
    }
}
