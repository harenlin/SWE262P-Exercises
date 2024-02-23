import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Iterators {

	public static class CharactersIterator implements Iterator<Character> {
		private BufferedReader reader;

		public CharactersIterator(String filename) throws IOException {
			this.reader = new BufferedReader(new FileReader(filename));
		}

		@Override
		public boolean hasNext() {
			try {
				return reader.ready();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public Character next() {
			try {
				int charCode = reader.read();
				return (charCode != -1) ? (char) charCode : null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static class AllWordsIterator implements Iterator<String> {
		private CharactersIterator charactersIterator;
	    private String currentWord;

		public AllWordsIterator(String filename) throws IOException {
			this.charactersIterator = new CharactersIterator(filename);
		}

		@Override
		public boolean hasNext() {
			boolean startChar = true;
			StringBuilder wordBuilder = new StringBuilder();

			while( this.charactersIterator.hasNext() ){
				char c = this.charactersIterator.next();

				if( startChar ){
					if( Character.isLetterOrDigit(c) ){ // We found the start of a word
						wordBuilder.append(Character.toLowerCase(c));
						startChar = false;
					} else {
						continue;
					}
				} else {
					if( Character.isLetterOrDigit(c) ){
						wordBuilder.append(Character.toLowerCase(c));
					} else { // We found end of word, emit it
						startChar = true;
						this.currentWord = wordBuilder.toString();
						return true;
					}
				}
			}

			return false;
		}

		@Override
		public String next() {
			return this.currentWord;
		}
	}

	public static class NonStopWordsIterator implements Iterator<String> {
		private AllWordsIterator allWordsIterator;
		private Set<String> stopWords;
		private String currentWord;

		public NonStopWordsIterator(String filename) throws IOException {
			this.allWordsIterator = new AllWordsIterator(filename);
			this.stopWords = new HashSet<>();
			try (BufferedReader reader = new BufferedReader(new FileReader("../stop_words.txt"))) {
	            String stopWordsString = reader.readLine();
    	        if(stopWordsString != null){
        	        List<String> stopWordsList = Arrays.asList(stopWordsString.split(","));
            	    this.stopWords.addAll(stopWordsList);
	            }
    	    } catch (IOException e) {
        	    e.printStackTrace();
	        }	
			for (char c = 'a'; c <= 'z'; c++) { this.stopWords.add(String.valueOf(c)); }
		}

		@Override
		public boolean hasNext() {
			while( this.allWordsIterator.hasNext() ){
				String w = this.allWordsIterator.next();
				if( this.stopWords.contains(w) ) continue;
				this.currentWord = w;
				return true;
			} return false;
		}

		@Override
		public String next() {
			return this.currentWord;
		}
	}

	public static class CountAndSortIterator implements Iterator<List<Map.Entry<String, Integer>>> {
		private NonStopWordsIterator nonStopWordsIterator;
		private Map<String, Integer> freqs;
		private int i;
		private List<Map.Entry<String, Integer>> sortedEntries;
		private boolean EOFSignal = false;

		public CountAndSortIterator(String filename) throws IOException {
			this.nonStopWordsIterator = new NonStopWordsIterator(filename);
			this.freqs = new HashMap<>();
			this.i = 1;
		}

		@Override
		public boolean hasNext() {
			while( this.nonStopWordsIterator.hasNext() ){
				String w = this.nonStopWordsIterator.next();
				this.freqs.put(w, this.freqs.getOrDefault(w, 0) + 1);

				if( ((this.i) % 5000) == 0 ){
					this.sortedEntries = new ArrayList<>(this.freqs.entrySet());
					Collections.sort(this.sortedEntries, 
						Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
					this.i += 1;
					return true;
				} else {
					this.i += 1;
				}
			}

			if( !this.nonStopWordsIterator.hasNext() && this.EOFSignal == false ){
				this.EOFSignal = true;
				this.sortedEntries = new ArrayList<>(this.freqs.entrySet());
				Collections.sort(this.sortedEntries, 
					Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
				return true;
			} 
			
			return false;
		}

		@Override
		public List<Map.Entry<String, Integer>> next() {
			return this.sortedEntries;
		}
	}


	private static String[] PRINT_CHOICE = {"FINAL_ROPORT_ONLY", "INCLUDE_TEMPORARY_REPORT"};

	public static void main(String[] args) {
		 
		/*
		// testing for CharactersIterator
		try {
			CharactersIterator iterator = new CharactersIterator(args[0]);
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		*/
		// testing for AllWordsIterator
		try {
			AllWordsIterator iterator = new AllWordsIterator(args[0]);
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		/*
		// testing for NonStopWordsIterator
		try {
			NonStopWordsIterator iterator = new NonStopWordsIterator(args[0]);
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		*/

		/*
		try {
			if( args[1].equals(PRINT_CHOICE[0]) ){
				CountAndSortIterator iterator = new CountAndSortIterator(args[0]);
				String res = "";
				while( iterator.hasNext() ){
					List<Map.Entry<String, Integer>> word_freqs = iterator.next();
					StringBuilder resBuilder = new StringBuilder();
					for(Map.Entry<String, Integer> entry : word_freqs.subList(0, 25)) {
						resBuilder.append(entry.getKey() + "  -  " + entry.getValue() + "\n");
					} res = resBuilder.toString();
				}
				System.out.print(res);
			} else if ( args[1].equals(PRINT_CHOICE[1]) ) {
				CountAndSortIterator iterator = new CountAndSortIterator(args[0]);
				// String res = "";
				while( iterator.hasNext() ){
					List<Map.Entry<String, Integer>> word_freqs = iterator.next();
					System.out.println("-----------------------------");
					// StringBuilder resBuilder = new StringBuilder();
					// resBuilder.append("-----------------------------\n");
					for(Map.Entry<String, Integer> entry : word_freqs.subList(0, 25)) {
						System.out.println(entry.getKey() + "  -  " + entry.getValue());
						// resBuilder.append(entry.getKey() + "  -  " + entry.getValue() + "\n");
					} // res = resBuilder.toString();
				}
				// System.out.print(res);
			} else {
				System.err.println("Please specify the printing method you want! Thanks!");
				System.exit(0);
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}  */
	}
}

