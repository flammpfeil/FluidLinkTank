package mods.flammpfeil.fluidlinktank;

import cpw.mods.fml.common.SidedProxy;

public class InitProxy {
	@SidedProxy(clientSide = "mods.flammpfeil.fluidlinktank.InitProxyClient", serverSide = "mods.flammpfeil.fluidlinktank.InitProxy")
	public static InitProxy proxy;


	public void initializeItemRenderer() {}

}
