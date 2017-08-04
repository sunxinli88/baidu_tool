package javaEyeStudy;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class SimpleResourceManager{  
	   private final InnerSynchronizer synchronizer;  
	   private static class InnerSynchronizer extends AbstractQueuedSynchronizer{  
	      InnerSynchronizer(int numOfResources){  
	         setState(numOfResources);  
	      }  
	      protected int tryAcquireShared(int acquires){  
	         for(;;){  
	            int available = getState();  
	            int remain = available - acquires;  
	            if(remain <0 || compareAndSetState(available, remain)){  
	               return remain;  
	            }  
	         }  
	      }  
	      protected boolean tryReleaseShared(int releases){  
	         for(;;){  
	            int available = getState();   
	            int next = available + releases;   
	            if(compareAndSetState(available,next)){  
	               return true;  
	            }  
	         }
	     
	      }  
	   }  
	   
	   
	   public SimpleResourceManager(int numOfResources){  
	      synchronizer = new InnerSynchronizer(numOfResources);  
	   }  
	   public void acquire() throws InterruptedException{  
	      synchronizer.acquireSharedInterruptibly(1);  
	   }        
	   public void release(){      
	      synchronizer.releaseShared(1);  
	    }  
	}
