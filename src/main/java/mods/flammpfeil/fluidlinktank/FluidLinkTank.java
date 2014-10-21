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
import net.minecraftforge.oredict.ShapelessOreRecipe;

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
    }

}
