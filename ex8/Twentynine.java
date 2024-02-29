import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.concurrent.ArrayBlockingQueue;

public class Twentynine {

	// @SuppressWarnings("unused")
	abstract static class ActiveWFObject extends Thread {
		public String name;
		public Queue<Object[]> queue;
		private boolean stopMe;

		public ActiveWFObject() {
			super();
			this.name = this.getClass().toString();
			this.queue = new ArrayBlockingQueue<>(100);
			this.stopMe = false;
			start();
		}

		public void run() {
			while( !this.stopMe ){
				Object[] message = queue.poll();
				if( message != null ){
					this.dispatch(message);
					if( ((String) message[0]).equals("die") ){
						this.stopMe = true;
					}
				}
			}
		}

		abstract void dispatch(Object[] message);

		public void send(ActiveWFObject receiver, Object[] message) {
			receiver.queue.offer(message);
		}
	}


	static class WordFrequencyManager extends ActiveWFObject {
		// Keeps the word frequency data 

		private Map<String, Integer> wordFreqs;

		public WordFrequencyManager() {
			this.wordFreqs = new HashMap<>();
		}

		protected void dispatch(Object[] message) {
			if( ((String) message[0]).equals("word") ){
				this.increment_count(Arrays.copyOfRange(message, 1, message.length));
			} else if ( ((String) message[0]).equals("top25") ){
				this.top25(Arrays.copyOfRange(message, 1, message.length));
			}
		}

		private void increment_count(Object[] message) {
			String word = (String) message[0];
			if( wordFreqs.containsKey(word) ){
				this.wordFreqs.put(word, this.wordFreqs.get(word) + 1);
			} else {
				this.wordFreqs.put(word, 1);
			}
		}

		private void top25(Object[] message) {
			ActiveWFObject recipient = (ActiveWFObject) message[0];
			List<Map.Entry<String, Integer>> freqs_sorted = this.wordFreqs
				.entrySet().stream().sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed()).collect(Collectors.toList());
			this.send(recipient, new Object[]{"top25", freqs_sorted});
		}
	}

	/* 
	   class StopWordManager extends ActiveWFObject {

	   }
	 */

	public static void main(String[] args) throws InterruptedException {
		WordFrequencyManager word_freq_manager = new WordFrequencyManager();
		System.out.println(word_freq_manager.toString());
		word_freq_manager.join();
	}
}
