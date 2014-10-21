package mods.flammpfeil.fluidlinktank;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by Furia on 14/10/20.
 */
public class BlockLinkTank extends BlockContainer {
    protected BlockLinkTank(Material p_i45386_1_) {
        super(p_i45386_1_);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);

        exit: if(tile instanceof TileLinkTank) {
            TileLinkTank tank = (TileLinkTank)tile;
            if(tank.storage == null) break exit;
            if(tank.storage.getTank().getFluid() == null) break exit;

            return tank.storage.getTank().getFluid().getFluid().getLuminosity();
        }

        return super.getLightValue(world, x, y, z);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileLinkTank();
    }

    static public TileLinkTank harvestingTile = null;
    @Override
    public void onBlockHarvested(World p_149681_1_, int p_149681_2_, int p_149681_3_, int p_149681_4_, int p_149681_5_, EntityPlayer p_149681_6_) {
        harvestingTile = (TileLinkTank)p_149681_1_.getTileEntity(p_149681_2_,p_149681_3_,p_149681_4_);
        super.onBlockHarvested(p_149681_1_, p_149681_2_, p_149681_3_, p_149681_4_, p_149681_5_, p_149681_6_);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> list = super.getDrops(world, x, y, z, metadata, fortune);

        for(ItemStack stack : list){
            if(stack.getItem() instanceof ItemBlockLinkTank){
                TileLinkTank tile =  (TileLinkTank)world.getTileEntity(x,y,z);
                if(tile == null)
                    tile = harvestingTile;

                if(tile.linkTankKey != null)
                    ItemBlockLinkTank.setLinkTankKey(stack, tile.linkTankKey);
                else if(tile.linkedFluid != null){
                    ItemBlockLinkTank.setFluid(stack, tile.linkedFluid);
                }
            }
        }
        harvestingTile = null;
        return list;
    }

    @Override
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {

        ItemStack stack = p_149727_5_.getHeldItem();
        exit: if(stack != null){
            if(stack.getItem() != Items.cauldron) break exit;

            if(p_149727_1_.isRemote)
                return true;

            TileLinkTank tile = (TileLinkTank)p_149727_1_.getTileEntity(p_149727_2_,p_149727_3_,p_149727_4_);

            if(tile == null) break exit;
            if(tile.storage == null)break exit;

            int capa = tile.storage.getTank().getCapacity();

            int newCapa = capa + FluidLinkTank.TankDefaultAmount;
            newCapa = Math.min(newCapa,FluidLinkTank.TankMaxAmount);

            if(capa == newCapa)
                break exit;

            tile.storage.getTank().setCapacity(newCapa);
            p_149727_1_.playSoundAtEntity(p_149727_5_, "mob.blaze.hit", 1.0F, 1.0F);

            if (!p_149727_5_.capabilities.isCreativeMode) {
                p_149727_5_.inventory.setInventorySlotContents(p_149727_5_.inventory.currentItem, null);
            }
            return true;
        }

        return super.onBlockActivated(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, p_149727_5_, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
    }
}
