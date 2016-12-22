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
			
			if(key.contains("����") || key.contains("Ӣ����") ){
				head.add(key);
				list.add(value);
				
			}
		

			System.out.println(key+":"+value.length());
			
		}

		if (head.size() != 0) { // �Д�����Л]��ˎƷ����head��=null�Y���ǲ�һ�ӵ�
			

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
