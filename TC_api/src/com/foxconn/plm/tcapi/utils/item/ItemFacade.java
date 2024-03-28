package com.foxconn.plm.tcapi.utils.item;
/**
* @author infodba
* @version 创建时间：2022年1月24日 下午4:09:53
* @Description
*/
public class ItemFacade {
	
	public final static ItemCreator creatorService = new ItemCreator();
    public final static ItemSearcher searcherService = new ItemSearcher();
    public final static ItemModifier modifierService = new ItemModifier();
}
