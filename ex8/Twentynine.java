import java.io.*;
import java.nio.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Twentynine {

	// @SuppressWarnings("unused")
	abstract static class ActiveWFObject extends Thread {
		public String name;
		public BlockingQueue<Object[]> queue;
		protected boolean stopMe;

		public ActiveWFObject() {
			super();
			this.name = this.getClass().toString();
			this.queue = new LinkedBlockingQueue<>();
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
	}

	static public void send(ActiveWFObject receiver, Object[] message) {
		receiver.queue.add(message);
	}
 
	static class StopWordManager extends ActiveWFObject {
    	/* Models the stop word filter */
		private Set<String> stopwords;
		public WordFrequencyManager word_freqs_manager;

		public StopWordManager() {
			super();
			this.stopwords = new HashSet<>();
		}

		protected void dispatch(Object[] message) {
			if( ((String) message[0]).equals("init") ){
				this.init(Arrays.copyOfRange(message, 1, message.length));	
			} else if ( ((String) message[0]).equals("filter") ) {
				this.filter(Arrays.copyOfRange(message, 1, message.length));
			} else { // forward
				send(this.word_freqs_manager, message);
			}
		}

		private void init(Object[] message){
			// System.out.println("Stop Word Manager Init");
			try(BufferedReader stopwordsReader = new BufferedReader(new FileReader("./../stop_words.txt"))){
				String[] words = stopwordsReader.readLine().split(",");
				for(String stopword : words){
					this.stopwords.add(stopword.trim());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(char c = 'a'; c <= 'z'; c++) this.stopwords.add(String.valueOf(c));
			this.word_freqs_manager = (WordFrequencyManager) message[0];
		}

		private void filter(Object[] message){
        	String word = (String) message[0];
			if( !this.stopwords.contains(word) )
				send(this.word_freqs_manager, new Object[]{"word", word});
		}
	}

	static class WordFrequencyManager extends ActiveWFObject {
		// Keeps the word frequency data 
		private Map<String, Integer> wordFreqs;

		public WordFrequencyManager() {
			super();
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
			// ActiveWFObject recipient = (ActiveWFObject) message[0];
			WordFrequencyController recipient = (WordFrequencyController) message[0];
			List<Map.Entry<String, Integer>> freqs_sorted = this.wordFreqs
				.entrySet().stream().sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed()).collect(Collectors.toList());
			send(recipient, new Object[]{"top25", freqs_sorted});
		}
	}

	static class DataStorageManager extends ActiveWFObject {
		// Models the contents of the file
		private String data;
		public StopWordManager stop_word_manager;

		public DataStorageManager(){
			super();
			this.data = "";
		}
		
		protected void dispatch(Object[] message) {
			if( ((String) message[0]).equals("init") ){
				this.init(Arrays.copyOfRange(message, 1, message.length));	
			} else if ( ((String) message[0]).equals("send_word_freqs") ) {
				this.process_words(Arrays.copyOfRange(message, 1, message.length));
			} else { // forward
				send(this.stop_word_manager, message);
			}
		}

		private void init(Object[] message){
			// System.out.println("Storage Manager Init");
			String path_to_file = (String) message[0];
			this.stop_word_manager = (StopWordManager) message[1];
			try {
				byte[] dataBytes = Files.readAllBytes(Paths.get(path_to_file));
				this.data = new String(dataBytes, "UTF-8");
				Pattern pattern = Pattern.compile("[\\W_]+");
				this.data = pattern.matcher(this.data).replaceAll(" ").toLowerCase();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		private void process_words(Object[] message){
			// System.out.println("Storage Manager Process Words");
			ActiveWFObject recipient = (ActiveWFObject) message[0];
			String data_str = String.join("", this.data);
			String[] words = data_str.split(" "); // "\\s+"
			for(String w : words){
				send(this.stop_word_manager, new Object[]{"filter", w});
			}
			send(this.stop_word_manager, new Object[]{"top25", recipient});
		}
	} 

	@SuppressWarnings("unchecked")
	static class WordFrequencyController extends ActiveWFObject {
		public DataStorageManager storage_manager;

		public WordFrequencyController(){
			super();
		}

		protected void dispatch(Object[] message) {
			if( ((String) message[0]).equals("run") ){
				this._run(Arrays.copyOfRange(message, 1, message.length));	
			} else if ( ((String) message[0]).equals("top25") ) {
				this.display(Arrays.copyOfRange(message, 1, message.length));
			} else { 
				throw new IllegalArgumentException("Message not understood: " + message[0]);// .toString());
			}
		}

		private void _run(Object[] message){
			this.storage_manager = (DataStorageManager) message[0];
			send(this.storage_manager, new Object[]{"send_word_freqs", this});
		}

		private void display(Object[] message){
			List<Map.Entry<String, Integer>> word_freqs = (List<Map.Entry<String, Integer>>) message[0];
			word_freqs.stream().limit(25).forEach(entry -> System.out.println(entry.getKey() + "  -  " + entry.getValue())); 
			send(this.storage_manager, new Object[]{"die"});
			this.stopMe = true;
		}
	} 

	public static void main(String[] args) throws InterruptedException {
		if( args.length == 0 ){
			// args = new String[]{"./../pride-and-prejudice.txt"};
			System.err.println("<Usage> java Twentynine ./path/to/file");
			System.exit(1);
		}

		WordFrequencyManager word_freq_manager = new WordFrequencyManager();
		// System.out.println(word_freq_manager);

		StopWordManager stop_word_manager = new StopWordManager();
		// System.out.println(stop_word_manager);
		send(stop_word_manager, new Object[]{"init", word_freq_manager});

		DataStorageManager storage_manager = new DataStorageManager();
		// System.out.println(storage_manager);
		send(storage_manager, new Object[]{"init", args[0], stop_word_manager});

		WordFrequencyController wfcontroller = new WordFrequencyController();
		// System.out.println(wfcontroller);
		send(wfcontroller, new Object[]{"run", storage_manager});

		// Wait for the active objects to finish
		word_freq_manager.join();
		stop_word_manager.join();
		storage_manager.join();
		wfcontroller.join();
	}
}
