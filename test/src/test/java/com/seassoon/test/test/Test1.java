package com.seassoon.test.test;

public class Test1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int[] arr = {1,2,3,4,5,6,7,8,9,10};
		
		int num=0;
		
		for(int i=0;i<arr.length;i++){
			for(int j=i+1;j<arr.length;j++){
				
				num++;
			}
		}
		System.out.println(num);
	}

}
