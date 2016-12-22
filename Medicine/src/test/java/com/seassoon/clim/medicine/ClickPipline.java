package com.seassoon.clim.medicine;

import java.util.HashMap;
import java.util.Map;

import com.seassoon.utils.MongoDBUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class ClickPipline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		// TODO Auto-generated method stub
		Map<String, Object> map = resultItems.getAll();
		
		if(map.size()>5){
			
			//��click��ȡ�Ľ���������ݿ�
			MongoDBUtils mongo = MongoDBUtils.getInstance("127.0.0.1", 27017);
			
			mongo.insert("medicine", "clickContent", map);
			
			
			//�������ݿ��е���Ϣ��state����Ϊ1��ʾ���������Ѿ���ȡ�����
			Map<String, Object> filterMap = new HashMap<String, Object>();
			
			Map<String, Object> updateMap = new HashMap<String, Object>();
			
			filterMap.put("url", map.get("url"));
			
			updateMap.put("state", "1");
			
			mongo.updateOne("medicine", "clickUrls", filterMap, updateMap);
		}
	}

}
