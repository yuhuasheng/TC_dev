package com.foxconn.plm.tcapi.utils.bom;

public class BOMFacade {
	
	public static final BOMCreator createService = new BOMCreator();
	public static final BOMModifier modifyService = new BOMModifier();
	public static final BOMSearcher searchService = new BOMSearcher();
	public static final BOMCommonOperate commonOperate = new BOMCommonOperate();
}
