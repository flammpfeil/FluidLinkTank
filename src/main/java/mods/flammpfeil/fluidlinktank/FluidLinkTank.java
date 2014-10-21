package mods.flammpfeil.fluidlinktank;

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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(name= FluidLinkTank.modname, modid= FluidLinkTank.modid, version= FluidLinkTank.version)
public class FluidLinkTank {

	public static final String modname = "FluidLinkTank";
    public static final String modid = "flammpfeil.fluidlinktank";
    public static final String version = "@VERSION@";

    public static Block linkTank;

	public static Configuration mainConfiguration;

    public static int TankDefaultAmount = 20000;
    public static int TankMaxAmount = 2000000000;

    public static final String LinkTankKeyStr = "LinkTankKey";
    public static final String LinkedFluidStr = "LinkedFluid";

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt){
        mainConfiguration = new Configuration(evt.getSuggestedConfigurationFile());

        try{
            mainConfiguration.load();

            {
                Property prop = mainConfiguration.get(Configuration.CATEGORY_GENERAL,"TankDefaultAmount", TankDefaultAmount);
                TankDefaultAmount = prop.getInt();
            }
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

        GameRegistry.registerBlock(linkTank, ItemBlockLinkTank.class, "LinkTank");

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

        {//base Universal
            ItemStack result = stackLinkTank.copy();
            GameRegistry.addRecipe(new ShapedOreRecipe(result,
                    "XBX",
                    "ECE",
                    "XBX",
                    'C',new ItemStack(Items.cauldron),
                    'B',new ItemStack(Items.bucket),
                    'E',new ItemStack(Items.ender_eye),
                    'X',new ItemStack(Items.quartz)));
        }

        {//named
            String[] dyes =
                    {
                            "Black",
                            "Red",
                            "Green",
                            "Brown",
                            "Blue",
                            "Purple",
                            "Cyan",
                            "LightGray",
                            "Gray",
                            "Pink",
                            "Lime",
                            "Yellow",
                            "LightBlue",
                            "Magenta",
                            "Orange",
                            "White"
                    };

            for(String color : dyes){
                ItemStack result = stackLinkTank.copy();
                ItemBlockLinkTank.setLinkTankKey(result, color);
                GameRegistry.addRecipe(new ShapelessOreRecipe(result, stackLinkTank , "paneGlass" + color));
            }
        }

        ItemStack itemLinkTank = stackLinkTank.copy();
        fluidContainerInnner = new FluidContainerInnner(new FluidStack(0,0),itemLinkTank);
        FluidContainerRegistry.registerFluidContainer(fluidContainerInnner.getFluid(),fluidContainerInnner.getFilled());
        fluidContainerInnner.getFilled().setItemDamage(OreDictionary.WILDCARD_VALUE-1);
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

        public FluidContainerInnner updateFluidAmount(Fluid targetFluid, int amount){
            this.fluid.fluidID = targetFluid.getID();
            this.fluid.amount = amount;
            ItemBlockLinkTank.setFluid(this.filled,targetFluid);
            filled.setItemDamage(0);
            return this;
        }

        public FluidContainerInnner setLinkTankKey(String name){
            ItemBlockLinkTank.setLinkTankKey(filled,name);
            return this;
        }
        public FluidContainerInnner reset(){
            this.fluid.fluidID = 0;
            this.fluid.amount = 0;
            filled.setTagCompound(null);
            filled.setItemDamage(OreDictionary.WILDCARD_VALUE - 1);
            return this;
        }


    }

    public static FluidContainerInnner fluidContainerInnner = null;

}
