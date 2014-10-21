package mods.flammpfeil.fluidlinktank;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by Furia on 14/10/20.
 */
public class BlockLinkTank extends BlockContainer {
    protected BlockLinkTank(Material p_i45386_1_) {
        super(p_i45386_1_);
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
}
