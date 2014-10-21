package mods.flammpfeil.fluidlinktank;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Furia on 14/10/21.
 */
public class FluidTankStorageWrapper extends FluidTank {
    protected StorageLinkTank storage;
    public FluidTankStorageWrapper(StorageLinkTank storage, int capacity) {
        super(capacity);

        this.storage = storage;
    }

    public void markDirty(){
        if(storage != null)
            storage.markDirty();
    }

    @Override
    public void setFluid(FluidStack fluid) {
        super.setFluid(fluid);
        markDirty();
    }

    @Override
    public void setCapacity(int capacity) {
        super.setCapacity(capacity);
        markDirty();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int result = super.fill(resource, doFill);
        if(doFill)
            markDirty();
        return result;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack result = super.drain(maxDrain, doDrain);
        if(doDrain)
            markDirty();

        return result;
    }
}
