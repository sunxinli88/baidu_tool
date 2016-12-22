package com.seassoon.clim.medicine;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.Db;
import com.seassoon.common.config.JfinalORMConfig;
import com.seassoon.common.model.Medicine;
import com.seassoon.utils.ConnectionDB;
import com.seassoon.utils.MongoDBUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class MagicPipline implements Pipeline  {

	@Override
	public void process(ResultItems resultItems, Task task) {
		// TODO Auto-generated method stub
		Map<String, Object> map = resultItems.getAll();
		
		String page = map.get("page").toString();
		
		if(map.size()>5){
			
//////			116.231.72.162
			MongoDBUtils mongo = MongoDBUtils.getInstance("127.0.0.1", 27017);
			
			mongo.insert("medicine", "spider", map);
		}
		if(map.size()>5){
			
			if(page.contains("http://drugs.dxy.cn/dwr/call/plaincall/DrugUtils.showDetail.dwr?")){
				
				int index = page.indexOf('=');

				int index2 = page.indexOf('x', index);

				// 获取所在页面的url中的数字
				String id = page.substring(index+1, index2);
				
				String url = "http://drugs.dxy.cn/drug/"+id+".htm";
			}
			
			Utils.addUrl(page);
		}
		
		
		
		
	}

}
