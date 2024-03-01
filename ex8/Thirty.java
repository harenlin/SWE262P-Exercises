import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;


public class Thirty {

	// Two data spaces
	private static Queue<String> word_space = new ConcurrentLinkedQueue<>();
	private static Queue<Map<String, Integer>> freq_space = new ConcurrentLinkedQueue<>();

	private static Set<String> stopwords;

	static {
		stopwords = new HashSet<>();
		try(BufferedReader stopWordsReader = new BufferedReader(new FileReader("./../stop_words.txt"))){
			String[] words = stopWordsReader.readLine().split(",");
			for(String stopword : words) {
				stopwords.add(stopword.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Worker function that consumes words from the word space and sends partial results to the frequency space
	private static void process_words() {
		Map<String, Integer> word_freqs = new HashMap<>();
		while( true ){
			// ==========================================================
			// Better to maintain time out here!
			// ==========================================================
			String word = word_space.poll();
			if( word == null ) {
				break;
			}
			if( !stopwords.contains(word) ){
				if( word_freqs.containsKey(word) ){
					word_freqs.put(word, word_freqs.get(word) + 1);
				} else {
					word_freqs.put(word, 1);
				}
			}
		}
		freq_space.add(word_freqs);
	}


	public static void main(String[] args) throws InterruptedException {
		// Let's have this thread populate the word space
		try {
			Files.lines(Paths.get(args[0]))
				.flatMap(line -> Pattern.compile("[a-z]{2,}").matcher(line.toLowerCase()).results())
				.map(matchResult -> matchResult.group())
				.forEach(word_space::add);
			// word_space.forEach(System.out::println);
		} catch (IOException e) {
			throw new RuntimeException("Error reading input file", e);
		}

		// Let's create the workers and launch them at their jobs
		Thread[] workers = new Thread[5];
		for(int i = 0; i < workers.length; i++){
			workers[i] = new Thread(Thirty::process_words);
			workers[i].start();
		}

		// Let's wait for the workers to finish
		for(Thread worker : workers){
			worker.join();
		} // System.out.println("All workers are done!");

		// Let's merge the partial frequency results by consuming frequency data from the frequency space
		Map<String, Integer> word_freqs = new HashMap<>();
		while( !freq_space.isEmpty() ){
			Map<String, Integer> cur_freqs = freq_space.poll();
			for(Map.Entry<String, Integer> entry : cur_freqs.entrySet()){
				String key = entry.getKey();
				int value = entry.getValue();
				int count = word_freqs.getOrDefault(key, 0) + value;
				word_freqs.put(key, count);
			}
		}

		// Print the top 25 word frequencies
		word_freqs.entrySet().stream()
			.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
			.limit(25)
			.forEach(entry -> System.out.println(entry.getKey() + "  -  " + entry.getValue())); 
	}
}

