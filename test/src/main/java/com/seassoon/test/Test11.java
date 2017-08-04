package com.seassoon.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test11 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String JsonContext = new ReadFile().ReadFile("G:\\���ĳ�\\test.json");

		JSONArray jsonArray = JSONArray.fromObject(JsonContext);

		int size = jsonArray.size();

		System.out.println("Size: " + size);

		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			System.out.println("[" + i + "]name=" + jsonObject.get("name"));
			System.out.println("[" + i + "]package_name=" + jsonObject.get("package_name"));
			System.out.println("[" + i + "]check_version=" + jsonObject.get("check_version"));
		}
	}

}
