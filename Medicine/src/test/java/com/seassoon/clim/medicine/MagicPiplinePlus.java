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
		
		//ȷ����ȡ����Ϣ����ȫ��
		if (map.size() > 5) {

			//�洢ҩƷ��ϸҳ��ȡ�Ľ��
			new Spidered("medicine", "medicinePlus").save(map);
			
			//�洢��ȡ����ҩƷ��ϸҳ�����Ӳ�����Ŀ����ȥ�ظ���ȡ
			new Spidered("medicine", "urls").save(um);
		}
	}

}
