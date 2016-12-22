package com.seassoon.clim.medicine;

import java.util.HashMap;
import java.util.Map;

import com.seassoon.common.Spidered;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class MagicPiplinePlus implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		// TODO Auto-generated method stub
		Map<String, Object> map = resultItems.getAll();
		
		String url = map.get("pagePrameter").toString();
		
		Map<String,Object> um = new HashMap<String, Object>();
		
		um.put("url", url);
		
		//确保爬取的信息是完全的
		if (map.size() > 5) {

			//存储药品详细页爬取的结果
			new Spidered("medicine", "medicinePlus").save(map);
			
			//存储爬取过的药品详细页的连接参数，目的是去重复爬取
			new Spidered("medicine", "urls").save(um);
		}
	}

}
