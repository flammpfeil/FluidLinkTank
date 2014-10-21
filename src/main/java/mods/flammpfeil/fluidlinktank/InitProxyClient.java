package mods.flammpfeil.fluidlinktank;

import cpw.mods.fml.client.registry.ClientRegistry;
import mods.flammpfeil.fluidlinktank.client.RenderTank;

public class InitProxyClient extends InitProxy{
	@Override
	public void initializeItemRenderer() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileLinkTank.class, new RenderTank());
	}
}
