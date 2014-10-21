package mods.flammpfeil.fluidlinktank;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Created by Furia on 14/10/20.
 */
public class TileLinkTank extends TileFluidHandler {

    StorageLinkTank storage = null;
    String linkTankKey = null;
    Fluid linkedFluid = null;
    protected boolean overwritable = true;

    public boolean getOverwritable(){
        return this.overwritable;
    }
    public TileLinkTank setOverwritable(boolean enable){
        this.overwritable = enable;
        return this;
    }

    public void validStorage(FluidStack input){
        if(getOverwritable()){
            if(input != null){
                setStorage(input.getFluid());
            }
        }else{
            validStorage();
        }
    }
    public void validStorage(){
        if(linkTankKey != null && linkTankKey.trim().length() != 0){
            setStorage(linkTankKey);
        }

        if(storage == null && linkedFluid != null){
            setStorage(linkedFluid);
        }

        if(storage == null && this.tank.getFluid() != null){
            setStorage(this.tank.getFluid().getFluid());
        }
    }

    public TileLinkTank setStorage(String key){
        this.linkTankKey = key;
        this.linkedFluid = null;
        return setStorage(StorageLinkTank.getLinkFluidStorage(key));
    }
    public TileLinkTank setStorage(Fluid fluid){
        this.linkTankKey = null;
        this.linkedFluid = fluid;
        return setStorage(StorageLinkTank.getLinkFluidStorage(fluid));
    }
    protected TileLinkTank setStorage(StorageLinkTank storage){
        this.storage = storage;

        if(this.storage != null)
            this.tank = this.storage.getTank();

        this.overwritable = false;

        return this;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if(tag.hasKey(FluidLinkTank.LinkTankKeyStr)){
            String key = tag.getString(FluidLinkTank.LinkTankKeyStr);
            if(key.trim().length() != 0){
                setStorage(linkTankKey);
            }
        }

        if(storage == null){
            if(tag.hasKey(FluidLinkTank.LinkedFluidStr)){
                String name = tag.getString(FluidLinkTank.LinkedFluidStr);

                Fluid fluid = FluidRegistry.getFluid(name);
                setStorage(fluid);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        if(linkTankKey != null && linkTankKey.trim().length() != 0){
            tag.setString(FluidLinkTank.LinkTankKeyStr,linkTankKey);
        }
        if(linkedFluid != null){
            tag.setString(FluidLinkTank.LinkedFluidStr,linkedFluid.getName());
        }
    }


    /* IFluidHandler */
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        validStorage(resource);
        return super.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        validStorage(resource);
        return super.drain(from, resource, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        validStorage();
        return super.drain(from, maxDrain, doDrain);
    }
}
