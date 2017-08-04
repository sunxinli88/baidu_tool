package com.seassoon.test;

public class TrimeTest {
	public static void main(String[] args) throws Exception {
		
	}
	private String TrimHelper(char[] trimChars, int trimType)
	{
		String s = "sdsdsdfsfggred";
	    int num = s.length() - 1;
	    int startIndex = 0;
	    if (trimType != 1)
	    {
	        startIndex = 0;
	        while (startIndex < s.length())
	        {
	            int index = 0;
	            char ch = this[startIndex];
	            index = 0;
	            while (index < trimChars.Length)
	            {
	                if (trimChars[index] == ch)
	                {
	                    break;
	                }
	                index++;
	            }
	            if (index == trimChars.Length)
	            {
	                break;
	            }
	            startIndex++;
	        }
	    }
	    if (trimType != 0)
	    {
	        num = this.Length - 1;
	        while (num >= startIndex)
	        {
	            int num4 = 0;
	            char ch2 = this[num];
	            num4 = 0;
	            while (num4 < trimChars.Length)
	            {
	                if (trimChars[num4] == ch2)
	                {
	                    break;
	                }
	                num4++;
	            }
	            if (num4 == trimChars.Length)
	            {
	                break;
	            }
	            num--;
	        }
	    }
	    int length = (num - startIndex) + 1;
	    if (length == this.Length)
	    {
	        return this;
	    }
	    if (length == 0)
	    {
	        return Empty;
	    }
	    return this.InternalSubString(startIndex, length, false);
	}

}
