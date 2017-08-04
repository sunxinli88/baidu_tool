package jacksonTest;
public class Student {  
    /* 
     * 
     * Class Descripton goes here. 
     * 
     * @class Student 
     * @version  1.0 
     * @author   廖益平 
     * @time  2011-11-8 上午03:01:08 
     */  
      private int uid;  
      private String uname;  
      private String upwd;  
      private double number;  
      private boolean isstudent;  
    public int getUid() {  
        return uid;  
    }  
    public void setUid(int uid) {  
        this.uid = uid;  
    }  
    public String getUname() {  
        return uname;  
    }  
    public void setUname(String uname) {  
        this.uname = uname;  
    }  
    public String getUpwd() {  
        return upwd;  
    }  
    public void setUpwd(String upwd) {  
        this.upwd = upwd;  
    }  
    public double getNumber() {  
        return number;  
    }  
    public void setNumber(double number) {  
        this.number = number;  
    }  
    public boolean isIsstudent() {  
        return isstudent;  
    }  
    public void setIsstudent(boolean isstudent) {  
        this.isstudent = isstudent;  
    }  
    @Override  
    public String toString() {  
      
        return "uid="+uid+",name="+uname+",upwd="+upwd+",number="+number+",isStudent="+isstudent;  
    }  
      
        
}  
