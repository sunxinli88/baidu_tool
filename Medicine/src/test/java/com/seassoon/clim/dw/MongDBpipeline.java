package com.seassoon.clim.dw;

import java.util.Map;

import com.seassoon.test.test.MongoDBJDBC;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class MongDBpipeline implements Pipeline{

	@Override
	public void process(ResultItems resultItems, Task task) {
		// TODO Auto-generated method stub
		Map<String, Object> mm = resultItems.getAll();
		
		String tb = "medicine";
		
		MongoDBJDBC mongoDB = new MongoDBJDBC();
	
		mongoDB.insert(mm, tb);
		
	}

}
