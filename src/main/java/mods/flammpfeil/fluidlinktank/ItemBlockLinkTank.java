package mods.flammpfeil.fluidlinktank;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;

import java.util.List;

/**
 * Created by Furia on 14/10/21.
 */
public class ItemBlockLinkTank extends ItemBlock implements IFluidContainerItem{
    public ItemBlockLinkTank(Block p_i45328_1_) {
        super(p_i45328_1_);

        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public boolean getShareTag() {
        return true;
    }

    static public ItemStack updateDamageWithFluidId(ItemStack stack){

        StorageLinkTank storage = getStorage(stack);

        if(storage != null){
            FluidStack fs = storage.getTank().getFluid();
            if(fs != null)
                stack.setItemDamage(fs.fluidID);
        }

        return stack;
    }

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack) {
        String name = super.getItemStackDisplayName(par1ItemStack);

        if(hasLinkTankKey(par1ItemStack)){
            name += ":" + getLinkTankKey(par1ItemStack);
        }

        StorageLinkTank storage = getStorage(par1ItemStack);

        if(storage != null){

            FluidStack fluid = storage.getTank().getFluid();
            if(fluid != null){
                name += "(" + fluid.getFluid().getLocalizedName() + ")";
            }
        }

        return name;
    }

    public static boolean hasLinkTankKey(ItemStack stack){
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null)
            return false;

        if(!tag.hasKey(FluidLinkTank.LinkTankKeyStr))
            return false;

        String key = tag.getString(FluidLinkTank.LinkTankKeyStr);
        if(key.trim().length() == 0){
            tag.removeTag(FluidLinkTank.LinkTankKeyStr);
            return false;
        }

        return true;
    }
    public static String getLinkTankKey(ItemStack stack){
        if(hasLinkTankKey(stack)){
            NBTTagCompound tag = stack.getTagCompound();
            String key = tag.getString(FluidLinkTank.LinkTankKeyStr);
            return key;
        }
        return null;
    }

    public static StorageLinkTank getStorage(ItemStack stack){
        if(hasLinkTankKey(stack)){
            String key = getLinkTankKey(stack);
            return StorageLinkTank.getLinkFluidStorage(key);
        }

        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null)
            return null;

        if(!tag.hasKey(FluidLinkTank.LinkedFluidStr))
            return null;

        String fluidName = tag.getString(FluidLinkTank.LinkedFluidStr);
        if(fluidName.trim().length() == 0){
            tag.removeTag(FluidLinkTank.LinkedFluidStr);
            return null;
        }

        Fluid targetFluid = FluidRegistry.getFluid(fluidName);
        if(targetFluid == null){
            tag.removeTag(FluidLinkTank.LinkedFluidStr);
            return null;
        }

        StorageLinkTank result = StorageLinkTank.getLinkFluidStorage(targetFluid);

        if(result != null){
            if(result.getTank().getFluidAmount() == 0){
                result.getTank().setFluid(new FluidStack(targetFluid,0));
            }
        }

        return result;
    }

    public static boolean hasLinkedFluid(ItemStack stack){
        StorageLinkTank storage = getStorage(stack);
        if(storage == null)
            return false;

        if(storage.getTank().getFluid() == null)
            return false;

        return true;
    }

    public static void setLinkTankKey(ItemStack stack, String key){

        if(key == null || key.trim().length() == 0)
            return;

        stack.setTagInfo(FluidLinkTank.LinkTankKeyStr,new NBTTagString(key));
        stack.setTagInfo(FluidLinkTank.LinkedFluidStr,new NBTTagString(""));
    }
    public static void setFluid(ItemStack stack,Fluid fluid){
        if(fluid == null)
            return;

        stack.setTagInfo(FluidLinkTank.LinkTankKeyStr,new NBTTagString(""));
        stack.setTagInfo(FluidLinkTank.LinkedFluidStr, new NBTTagString(fluid.getName()));
    }


    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        updateDamageWithFluidId(stack);
        FluidLinkTank.FluidContainerInnner inner = FluidLinkTank.fluidContainerMap.get(stack.getItemDamage());
        StorageLinkTank storage = getStorage(stack);
        if(inner != null && storage != null){
            inner.updateFluidAmount(Math.min(storage.getTank().getFluidAmount(), FluidContainerRegistry.BUCKET_VOLUME));
            if(hasLinkTankKey(stack)){
                String key = getLinkTankKey(stack);
                inner.setLinkTankKey(key);
            }

        }
        return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        StorageLinkTank storage = getStorage(stack);
        if(storage == null)
            return false;

        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        StorageLinkTank storage = getStorage(stack);
        if(storage == null)
            return 1.0;

        IFluidTank tank = storage.getTank();

        return 1.0 - (double)tank.getFluidAmount() / (double)tank.getCapacity();
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        if(par2EntityPlayer.isSneaking())
            return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
        else
            return false;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        boolean result = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);

        TileLinkTank ltank = (TileLinkTank)world.getTileEntity(x,y,z);
        if(hasLinkTankKey(stack)){
            ltank.setStorage(getLinkTankKey(stack));
        }else{
            StorageLinkTank storage = getStorage(stack);
            if(storage != null){
                FluidStack fstack = storage.getTank().getFluid();
                if(fstack != null)
                    ltank.setStorage(fstack.getFluid());
            }
        }

        return result;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

        exit: if (movingobjectposition != null){
            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int i = movingobjectposition.blockX;
                int j = movingobjectposition.blockY;
                int k = movingobjectposition.blockZ;

                if (!par2World.canMineBlock(par3EntityPlayer, i, j, k))
                    break exit;

                if (!par3EntityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, par1ItemStack))
                    break exit;



                Fluid fluid = FluidRegistry.lookupFluidForBlock(par2World.getBlock(i,j,k));
                if(fluid == null)
                    break exit;

                Fluid targetFluid = null;
                {
                    StorageLinkTank storage = getStorage(par1ItemStack);

                    if(storage != null){
                        FluidStack stack = storage.getTank().getFluid();
                        if(stack != null)
                            targetFluid = stack.getFluid();

                    }
                }

                if(targetFluid == null && !hasLinkedFluid(par1ItemStack)){
                    if(!hasLinkTankKey(par1ItemStack))
                        setFluid(par1ItemStack,fluid);

                    targetFluid = fluid;
                }

                if(fluid != targetFluid)
                    break exit;

                int l = par2World.getBlockMetadata(i, j, k);
                if(l!=0)
                    break exit;

                if(par2World.isRemote)
                    break exit;

                StorageLinkTank storage = getStorage(par1ItemStack);
                if(storage == null)
                    break exit;

                int amount = FluidContainerRegistry.BUCKET_VOLUME;

                int fillAmount = storage.getTank().fill(new FluidStack(fluid,amount),false);

                if(fillAmount == amount){

                    par2World.setBlock(i, j, k, Blocks.air, 0, 2); //3 update block , 2 silent
                    storage.getTank().fill(new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), true);
                }
            }

        }

        return par1ItemStack;
    }

    @Override
    public FluidStack getFluid(ItemStack container) {

        if(!hasLinkedFluid(container))
            return null;
        StorageLinkTank storage = getStorage(container);
        if(storage == null)
            return null;

        FluidStack stack = storage.getTank().getFluid();

        if(stack == null)
            return null;

        if(stack.amount == 0)
            return null;

        return stack.copy();
    }

    @Override
    public int getCapacity(ItemStack container) {
        return FluidLinkTank.TankMaxAmount;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if(resource == null)
            return 0;


        if(!hasLinkTankKey(container)){
            if(!hasLinkedFluid(container)){
                setFluid(container,resource.getFluid());
            }
        }

        StorageLinkTank storage = getStorage(container);
        if(storage == null)
            return 0;

        return storage.getTank().fill(resource, doFill);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack stack = itemStack.copy();

        if(stack.stackSize <= 0)
            stack.stackSize = 1;

        FluidStack fs = FluidContainerRegistry.getFluidForFilledItem(stack);

        drain(stack, fs.amount, true);

        FluidLinkTank.FluidContainerInnner inner = FluidLinkTank.fluidContainerMap.get(stack.getItemDamage());
        if(inner !=null){
            inner.resetFluidAmount();
            inner.resetLinkTankKey();
        }

        return stack;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {

        StorageLinkTank storage = getStorage(container);
        if(storage == null)
            return null;

        if(storage.getTank().getFluidAmount() == 0)
            return null;

        return storage.getTank().drain(maxDrain,doDrain);
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        if(hasLinkTankKey(par1ItemStack)){
            par3List.add("TankKey : " + getLinkTankKey(par1ItemStack));
        }

        StorageLinkTank storage = getStorage(par1ItemStack);

        if(storage != null){

            FluidStack fluid = storage.getTank().getFluid();
            if(fluid != null){
                par3List.add("Fluid : " + fluid.getFluid().getLocalizedName());

                if(storage != null){
                    par3List.add("Amount : " + storage.getTank().getFluidAmount() + "mb");
                }
            }
        }
    }
}
