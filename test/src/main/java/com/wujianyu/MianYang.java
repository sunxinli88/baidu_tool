package com.wujianyu;

import java.util.List;
import java.util.Map;

import com.baidu.util.BaiduMapUtils;
import com.seassoon.utils.ConnectionDB;

public class MianYang {

	public static void main(String[] args) {
		ConnectionDB db = new ConnectionDB("jdbc:mysql://10.50.5.15:3306/db_court_extract?useUnicode=true&characterEncoding=utf8","user_c_extract","jnhuytwoql980lko");
		String sql ="select * from coordinate_information";
		List<Map<String, Object>> rs = db.excuteQuery(sql, null);
		String[] colums = {"","","",""};
		
		for(Map<String, Object> r:rs){
			String lonlat = r.get("coordinate").toString();
			
			int id = Integer.valueOf(r.get("id").toString());
			
			String[] xy = lonlat.split(",");
			
			lonlat = xy[1]+","+xy[0];
			
			Map<String, String> location = BaiduMapUtils.geocoder_location(lonlat);
			
			if(null!=location){
				String sql1 = "update coordinate_information set province = '"+location.get("province")
				+"',city = '"+location.get("city")+"',district = '"+location.get("district")+"',address = '"
				+location.get("address")+"',description = '"+location.get("description")
				+"' where uid = '"+r.get("uid")+"' and id = '"+id+"'";
				db.executeUpdate(sql1);
			}
		}
	}
}
