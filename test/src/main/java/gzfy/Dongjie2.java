package gzfy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Dongjie2 {

	public static void main(String[] args) {
		List<String> x = new ArrayList<String>();
		x.add("a");
		x.add("b");
		x.add("c");
		x.add("d");
		String re = x.remove(1);
		for(String e:x){
			System.out.print(e+"\t");
		}
		System.out.println("\n"+re);
	}
	public static Double specialSort(){
		List<Double> arry = new LinkedList<Double>();
		
		arry.add(4.2);
		arry.add(24.2);
		arry.add(34.2);
		arry.add(14.2);
		arry.add(7.2);
		arry.add(23.2);
		arry.add(5.2);
//		arry.add(34.2);
		Double biaodi = 90.9;
		Double delta = -1.0;
		while(arry.size()>0&&biaodi>0){
			//取大于标的金额的最小值
			arry.sort(new Comparator<Double>() {
				@Override
				public int compare(Double o1, Double o2) {
					// TODO Auto-generated method stub
					if(o1>o2){
						return 1;
					}else{
						return -1;
					}
				}
			});
			
			System.out.println("\n删除前,标的金额"+biaodi);
			for(Double e:arry){
				System.out.print(e+"\t");
			}
			System.out.print("\n");
			for(Double e:arry){
				if(e>biaodi){
					delta = e;
					break;
				}
			}
			if(delta<0){
				delta = arry.get(arry.size()-1);
			}
			
			arry.remove(delta);
			if(biaodi<delta){
				delta = biaodi;
				break;
			}else{
				biaodi -= delta;
			}
			System.out.println("冻结金额："+delta+",标的金额"+biaodi);
			System.out.println("删除后：");
			for(Double e:arry){
				System.out.print(e+"\t");
			}
			delta = -1.0;
		}
		return delta;
	}
}
