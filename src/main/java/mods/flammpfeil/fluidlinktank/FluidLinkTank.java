package mods.flammpfeil.fluidlinktank;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.Map;

@Mod(name= FluidLinkTank.modname, modid= FluidLinkTank.modid, version= FluidLinkTank.version)
public class FluidLinkTank {

	public static final String modname = "FluidLinkTank";
    public static final String modid = "flammpfeil.fluidlinktank";
    public static final String version = "@VERSION@";

    public static Block linkTank;

	public static Configuration mainConfiguration;

    public static int TankMaxAmount = 20000;

    public static final String LinkTankKeyStr = "LinkTankKey";
    public static final String LinkedFluidStr = "LinkedFluid";

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt){
        mainConfiguration = new Configuration(evt.getSuggestedConfigurationFile());

        try{
            mainConfiguration.load();

            {
                Property prop = mainConfiguration.get(Configuration.CATEGORY_GENERAL,"TankMaxAmount", TankMaxAmount);
                TankMaxAmount = prop.getInt();

            }

        }
        finally
        {
            mainConfiguration.save();
        }

        linkTank = new BlockLinkTank(Material.redstoneLight)
                .setHardness(1.0F).setBlockName(modid +".LinkTank")
                .setBlockTextureName(modid+ ":" + "FluidLinkContainer")
                .setCreativeTab(CreativeTabs.tabRedstone);

        GameRegistry.registerBlock(linkTank,ItemBlockLinkTank.class,"LinkTank");

        GameRegistry.registerTileEntity(TileLinkTank.class, "flammpfeil.linktank");
    }


    /**
     * [\] -> [\\]
     * ["] -> [\quot;]
     * 改行 -> [\r;\r;]
     * 全文を""でquotationする
     * 上記のとおり、エスケープされます。直接configを修正するときに覚えておくべき。
     * @param source
     * @return
     */
    static private String escape(String source){
        if(source.length() == 0)
            return source;
        else
            return String.format("\"%s\"", source.replace("\\","\\\\").replace("\"","\\quot;").replace("\r", "\\r;").replace("\n", "\\n;"));
    }
    static private String unescape(String source){
        return source.replace("\"", "").replace("\\quot;", "\"").replace("\\r;","\r").replace("\\n;","\n").replace("\\\\", "\\");
    }

    @EventHandler
    public void init(FMLInitializationEvent evt){
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt)
    {
        ItemStack stackLinkTank = new ItemStack(linkTank);

        {
            ItemStack result = stackLinkTank.copy();
            ItemBlockLinkTank.setLinkTankKey(result, "TestA");
            GameRegistry.addRecipe(new ShapelessOreRecipe(result, result, new ItemStack(Items.iron_ingot)));
        }

        {
            ItemStack result = stackLinkTank.copy();
            ItemBlockLinkTank.setLinkTankKey(result, "TestB");
            GameRegistry.addRecipe(new ShapelessOreRecipe(result, result, new ItemStack(Items.gold_ingot)));
        }


        ItemStack itemLinkTank = stackLinkTank.copy();
        for(int i = 1 ;i < FluidRegistry.getMaxID(); i++){
            ItemStack curTank = itemLinkTank.copy();
            Fluid fluid = FluidRegistry.getFluid(i);

            ItemBlockLinkTank.setFluid(curTank,fluid);
            curTank.setItemDamage(i);

            FluidContainerInnner inner = new FluidContainerInnner(new FluidStack(fluid,0),curTank);
            fluidContainerMap.put(i,inner);

            FluidContainerRegistry.registerFluidContainer(inner.getFluid(),inner.getFilled());

        }
    }

    public static class FluidContainerInnner{
        ItemStack filled;
        FluidStack fluid;

        public FluidContainerInnner(FluidStack fluidStack, ItemStack filled){
            this.filled = filled;
            this.fluid = fluidStack;
        }

        public ItemStack getFilled() {
            return filled;
        }
        public FluidStack getFluid() {
            return fluid;
        }

        public FluidContainerInnner updateFluidAmount(int amount){
            this.fluid.amount = amount;
            return this;
        }

        public FluidContainerInnner resetFluidAmount(){
            this.fluid.amount = 0;
            return this;
        }

        public FluidContainerInnner setLinkTankKey(String name){
            ItemBlockLinkTank.setLinkTankKey(filled,name);
            return this;
        }
        public FluidContainerInnner resetLinkTankKey(){
            ItemBlockLinkTank.setFluid(filled,fluid.getFluid());
            return this;
        }


    }

    public static Map<Integer,FluidContainerInnner> fluidContainerMap = Maps.newHashMap();

}
