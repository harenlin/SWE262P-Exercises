import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

public class Streams {

	public static class CharactersSpliterator implements Spliterator<Character> {
        private BufferedReader reader;
        public int count;

        public CharactersSpliterator(String filename) throws IOException {
            this.count = 0;
            this.reader = new BufferedReader(new FileReader(filename));
        }

        public Stream<Character> stream() {
            return StreamSupport.stream(this, false);
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            this.count += 1;
            try {
                int charCode = reader.read();
                if (charCode != -1) {
                    action.accept((char) charCode);
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public Spliterator<Character> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return Spliterator.NONNULL | Spliterator.ORDERED | Spliterator.SIZED;
        }
	}

    public static class AllWordsSpliterator implements Spliterator<String> {
        private final CharactersSpliterator charactersSpliterator;
        private String currentWord;
        private boolean accepted;
        public int count;

        private boolean[] startChar = {true};
        private StringBuilder wordBuilder = new StringBuilder();

        public AllWordsSpliterator(String filename) throws IOException {
            this.accepted = false;
            this.count = 0;
            this.charactersSpliterator = new CharactersSpliterator(filename);
        }

        public Stream<String> stream() {
            return StreamSupport.stream(this, false);
        }

        @Override
        public boolean tryAdvance(Consumer<? super String> action) {
            // boolean[] startChar = {true};
            // boolean startChar = true; --> 
            // local variables referenced from a lambda expression must be final or effectively final
            // StringBuilder wordBuilder = new StringBuilder();

            /* while ( charactersSpliterator.tryAdvance(c -> {
                if (startChar[0]) {
                    if (Character.isLetterOrDigit(c)) {
                        wordBuilder.append(Character.toLowerCase(c));
                        startChar[0] = false;
                    }
                } else {
                    if (Character.isLetterOrDigit(c)) {
                        wordBuilder.append(Character.toLowerCase(c));
                    } else {
                        startChar[0] = true;
                        this.currentWord = wordBuilder.toString();
                        wordBuilder.setLength(0);
                        action.accept(this.currentWord);
                    }
                }
            })); // return true; */

            /* 
            // ===============
            while (true) {
                if ( !charactersSpliterator.tryAdvance(c -> {
                    if (startChar[0]) {
                        if (Character.isLetterOrDigit(c)) {
                            wordBuilder.append(Character.toLowerCase(c));
                            startChar[0] = false;
                        }
                    } else {
                        if (Character.isLetterOrDigit(c)) {
                            wordBuilder.append(Character.toLowerCase(c));
                        } else {
                            startChar[0] = true;
                            this.currentWord = wordBuilder.toString();
                            wordBuilder.setLength(0);
                            action.accept(this.currentWord);
                        }
                    }
                }) ) {
                    break;
                } 
            }
            if( wordBuilder.length() != 0 ){ // Check for the last word
                this.currentWord = wordBuilder.toString();
                wordBuilder.setLength(0);
                action.accept(this.currentWord);
                return true;
            }
            return false; 
            // =================
            */


            
            this.count += 1;
            if ( this.charactersSpliterator.tryAdvance(c -> {
                if (startChar[0]) {
                    if (Character.isLetterOrDigit(c)) {
                        wordBuilder.append(Character.toLowerCase(c));
                        startChar[0] = false;
                    }
                } else {
                    if (Character.isLetterOrDigit(c)) {
                        wordBuilder.append(Character.toLowerCase(c));
                    } else {
                        startChar[0] = true;
                        this.currentWord = wordBuilder.toString();
                        wordBuilder.setLength(0);
                        this.accepted = true;
                        action.accept(this.currentWord);
                    }
                }
            }) ) {
                if( this.accepted == true ){
                    this.accepted = false;
                    return true;
                }
                return this.tryAdvance(action);
            } 
            return false;
            
        }

        @Override
        public Spliterator<String> trySplit() {
            return null; 
        }

        @Override
        public long estimateSize() {
            return this.charactersSpliterator.estimateSize();
        }

        @Override
        public int characteristics() {
            return this.charactersSpliterator.characteristics();
        }
    }

    public static class NonStopWordsSpliterator implements Spliterator<String> {
        private AllWordsSpliterator allWordsSpliterator;
        private Set<String> stopWords;
        private String currentWord;
        private boolean accepted;
        public int count;
    
        public NonStopWordsSpliterator(String filename) throws IOException {
            this.accepted = false;
            this.allWordsSpliterator = new AllWordsSpliterator(filename);
            this.stopWords = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("./../stop_words.txt"))) {
                String stopWordsString = reader.readLine();
                if (stopWordsString != null) {
                    List<String> stopWordsList = Arrays.asList(stopWordsString.split(","));
                    this.stopWords.addAll(stopWordsList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (char c = 'a'; c <= 'z'; c++) {
                this.stopWords.add(String.valueOf(c));
            }
        }
    
        public Stream<String> stream() {
            return StreamSupport.stream(this, false);
        }

        @Override
        public boolean tryAdvance(Consumer<? super String> action) {
            // while( this.allWordsSpliterator.tryAdvance(w -> {
            //     if( this.stopWords.contains(w) == false ){
			// 	    this.currentWord = w;
            //         action.accept(this.currentWord);
            //     } 
            // })); //return true;
            // return false; 
            
            this.count += 1;
            if ( this.allWordsSpliterator.tryAdvance(w -> {
                if( this.stopWords.contains(w) == false ) {
				    this.currentWord = w;
                    this.accepted = true;
                    action.accept(this.currentWord);
                }
            })) {
                if( this.accepted == true ){
                    this.accepted = false;
                    return true;
                }
                // System.out.println("recursive call");
                return this.tryAdvance(action);
            }
            return false;
            
        }
    
        @Override
        public Spliterator<String> trySplit() {
            return null; // Not supporting parallel processing
        }
    
        @Override
        public long estimateSize() {
            return this.allWordsSpliterator.estimateSize();
        }
    
        @Override
        public int characteristics() {
            return this.allWordsSpliterator.characteristics();
        }
    }

    public static class CountAndSortSpliterator implements Spliterator<List<Map.Entry<String, Integer>>> {
        private NonStopWordsSpliterator nonStopWordsSpliterator; // Updated name
        private Map<String, Integer> freqs;
        private int i;
        private List<Map.Entry<String, Integer>> sortedEntries;
        private boolean EOFSignal = false;

        private boolean accepted;
        public int count;

        public CountAndSortSpliterator(String filename) throws IOException {
            this.accepted = false;
            this.nonStopWordsSpliterator = new NonStopWordsSpliterator(filename); // Updated name
            this.freqs = new HashMap<>();
            this.sortedEntries = new ArrayList<>(this.freqs.entrySet());
            this.i = 1;
        }

        public Stream<List<Map.Entry<String, Integer>>> stream() {
            return StreamSupport.stream(this, false);
        }

        @Override
        public boolean tryAdvance(Consumer<? super List<Map.Entry<String, Integer>>> action) {
            this.count += 1;
            while (this.nonStopWordsSpliterator.tryAdvance(word -> {
                this.freqs.put(word, this.freqs.getOrDefault(word, 0) + 1);
            })) {
                if ((this.i % 5000) == 0) {
                    this.sortedEntries = new ArrayList<>(this.freqs.entrySet());
                    this.sortedEntries.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
                    action.accept(this.sortedEntries);
                    this.i += 1;
                    return true;
                } else {
                    this.i += 1;
                }
            }

            if (!this.nonStopWordsSpliterator.tryAdvance(word -> {}) && !this.EOFSignal) {
                this.EOFSignal = true;
                this.sortedEntries = new ArrayList<>(this.freqs.entrySet());
                this.sortedEntries.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
                action.accept(this.sortedEntries);
                // return true;
                return false;
            }
            return false;
            

            /* 
            if( this.nonStopWordsSpliterator.tryAdvance(word -> {
                    this.freqs.put(word, this.freqs.getOrDefault(word, 0) + 1);
                    if ((this.i % 5000) == 0) {
                        this.sortedEntries.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
                        this.accepted = true;
                        action.accept(this.sortedEntries);
                        this.i += 1;
                    } else {
                        this.i += 1;
                    }
                }
            )) {
                if( this.accepted == true ){
                    this.accepted = false;
                    return true;
                }
                // System.out.println("recursive call");
                return this.tryAdvance(action);
            }

            // if (!this.nonStopWordsSpliterator.tryAdvance(word -> {} ) && !this.EOFSignal) {
            //     this.EOFSignal = true;
            //     this.sortedEntries = new ArrayList<>(this.freqs.entrySet());
            //     this.sortedEntries.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
            //     action.accept(this.sortedEntries);
            //     return true;
            // }
            
            return false;
            */
        }

        @Override
        public Spliterator<List<Map.Entry<String, Integer>>> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return this.nonStopWordsSpliterator.estimateSize();
        }

        @Override
        public int characteristics() {
            return this.nonStopWordsSpliterator.characteristics();
        }
    }

	public static void main(String[] args) {
        /*
        // testing for CharactersSpliterator
        CharactersSpliterator spliterator;
		try {
			spliterator = new CharactersSpliterator(args[0]);
			spliterator.stream().forEach(System.out::println);
            System.out.println(spliterator.count);
		} catch (IOException e) {
			e.printStackTrace();
		} 
        */

		/* 
        // testing for AllWordsSpliterator
        AllWordsSpliterator spliterator;
		try {
			spliterator = new AllWordsSpliterator(args[0]);
			spliterator.stream().forEach(System.out::println);
            System.out.println(spliterator.count);
		} catch (IOException e) {
			e.printStackTrace();
		} 
        */

        
        /*
        // testing for NonStopWordsSpliterator
        NonStopWordsSpliterator spliterator;
		try {
			spliterator = new NonStopWordsSpliterator(args[0]);
			spliterator.stream().forEach(System.out::println);
            System.out.println("tryAdvance count: " + spliterator.count);
		} catch (IOException e) {
			e.printStackTrace();
		} 
        */
        
         
        CountAndSortSpliterator spliterator;
		try {
			spliterator = new CountAndSortSpliterator(args[0]);
            spliterator.stream().forEach(list -> {
                System.out.println("-----------------------------");
                int cnt = 1;
                for(Map.Entry<String, Integer> entry : list){
                    System.out.println(entry.getKey() + "  -  " + entry.getValue());
                    if( cnt++ >= 25 ) break;
                }
            });
            // System.out.println("tryAdvance count: " + spliterator.count);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}

