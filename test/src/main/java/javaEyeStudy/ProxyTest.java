package javaEyeStudy;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Random;

public class ProxyTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Object[] elements = new Object[1000];
		for(int i=0;i<elements.length;i++){
			Integer value =i+1;
			Class[] interfaces = value.getClass().getInterfaces();
			InvocationHandler handler = new TranceHandler(value);
			Object proxy = Proxy.newProxyInstance(null, interfaces, handler);
			elements[i] = proxy;
			
		}
		Integer key = new Random().nextInt(elements.length)+1;
		
		int result = Arrays.binarySearch(elements, key);
		
		if(result>0)
			System.out.println(elements[result]);

		
//		int result = ((Comparable)elements[0]).compareTo(((Comparable)elements[1]));
		
		elements[0].toString();

	}

}

class TranceHandler implements InvocationHandler{
	
	public TranceHandler(Object t){
		target = t;
	}
	
	private Object target;
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		
		System.out.print(target);
		System.out.print("."+method.getName()+"(");
		
		if(args!=null){
			for(int i=0;i<args.length;i++){
				System.out.print(args[i]);
				if(i<args.length-1){
					System.out.println(",");
				}
			}
			System.out.println(")");
			
			return method.invoke(target, args);
		}
		
		
		return null;
	}
	
}
