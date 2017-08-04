package javaEyeStudy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;

public class PageSizeSorter {
	// 并发性能远远优于HashTable的
	// Map实现，hashTable做任何操作都需要获得锁，同一时间只有有个线程能使用，而ConcurrentHashMap是分段加锁，不同线程访问不同的数据段，完全不受影响，忘记HashTable吧。
	private static final ConcurrentHashMap<String, Integer> sizeMap = new ConcurrentHashMap<>();

	private void sort(){  
		List<Entry<String, Integer>> x = sizeMap.keySet();
	      List<Entry<String, Integer>> list = x;  
	      
	      Collections.sort(list, new Comparator<Entry<String,Integer>>(){  
	         public int compare (Entry<String, Integer> o1, Entry<String , Integer> o2){  
	            return Integer.compare(o2.getValue(),o1.getValue()); 
	         }
	      });
	      
	      System.out.println(Arrays.deepToString(list.toArray()));
	}

	private static class GetSizeWorker implements Runnable {

		private final String urlString;
		private CountDownLatch signal;

		public GetSizeWorker(String urlString, CountDownLatch signal) {
			this.urlString = urlString;
			this.signal = signal;
		}

		public void run() {
			try {
				InputStream is = new URL(urlString).openStream();
				int size = IOUtils.toByteArray(is).length;
				sizeMap.put(urlString, size);
			} catch (IOException e) {
				sizeMap.put(urlString, -1);
			} finally {
				signal.countDown();// 完成一个任务 ， 任务数-1
			}
		}
	}

	public void sortPageSize(Collection<String> urls) throws InterruptedException {
		CountDownLatch sortSignal = new CountDownLatch(urls.size());
		for (String url : urls) {
			new Thread(new GetSizeWorker(url, sortSignal)).start();
		}
		sortSignal.await();// 主线程在这里等待，任务数归0，则继续执行
		sort();
	}
}
