package javaEyeStudy;

import java.io.Console;

public class ConsoleTest {
	public static void main(String[] args){
		Console cons = System.console();
		
		String s = cons.readLine();
		System.out.println(s);
	}
}
