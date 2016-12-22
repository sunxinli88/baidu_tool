package com.seassoon.clim.dw;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.seassoon.test.test.MongoDBJDBC;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import util.connSql_2;

public class TestPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {

		Map<String, Object> mm = resultItems.getAll();

		String tb = "test_medicine";

		List<String> head = new ArrayList<String>();

		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : mm.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			
			if(key.contains("别名") || key.contains("英文名") ){
				head.add(key);
				list.add(value);
				
			}
		

			System.out.println(key+":"+value.length());
			
		}

		if (head.size() != 0) { // 判斷頁面中沒有藥品，和head！=null結果是不一樣的
			

			try {
				connSql_2 cm = new connSql_2();
				cm.insert(head, list, tb);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
