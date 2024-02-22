/* import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Iterators {

    static class CharacterGenerator implements Iterator<Character> {
        private final BufferedReader reader;
        private char nextChar;

        public CharacterGenerator(String filename) throws IOException {
            this.reader = new BufferedReader(new FileReader(filename));
            this.nextChar = getNextCharacter();
        }

        private char getNextCharacter() throws IOException {
            int nextCharInt = reader.read();
            return (nextCharInt == -1) ? 0 : (char) nextCharInt;
        }

        @Override
        public boolean hasNext() {
            return nextChar != 0;
        }


        @Override
        public Character next() {
            char currentChar = nextChar;
            try {
                nextChar = getNextCharacter();
            } catch (IOException e) {
                throw new NoSuchElementException("Error reading the next character");
            }
            return currentChar;
        }

        public void close() throws IOException {
            reader.close();
        }
    }

    static class WordIterator implements Iterator<String> {
        private final CharacterGenerator characterGenerator;
        private boolean startChar;

        public WordIterator(CharacterGenerator characterGenerator) {
            this.characterGenerator = characterGenerator;
            this.startChar = true;
        }

        @Override
        public boolean hasNext() {
            return startChar || characterGenerator.hasNext();
        } 


        @Override
        public String next() {
            StringBuilder wordBuilder = new StringBuilder();
            try {
                while (hasNext()) {
                    char c = characterGenerator.next();

                    if (startChar) {
                        if (Character.isLetterOrDigit(c)) {
                            // Found the start of a word
                            wordBuilder.append(Character.toLowerCase(c));
                            startChar = false;
                        }
                    } else {
                        if (Character.isLetterOrDigit(c)) {
                            wordBuilder.append(Character.toLowerCase(c));
                        } else {
                            // Found end of word
                            startChar = true;
                            return wordBuilder.toString();
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                // End of file
            }
            return null;
        }

        public void close() throws IOException {
            characterGenerator.close();
        }
    }

    static class StopWords {
        private final Set<String> stopWords;

        public StopWords() throws IOException {
            this.stopWords = new HashSet<>();
            BufferedReader stopWordsReader = new BufferedReader(new FileReader("../stop_words.txt"));
            String stopWordsString = stopWordsReader.readLine();
            stopWordsReader.close();
            String[] stopWordsArray = stopWordsString.split(",");
            Collections.addAll(stopWords, stopWordsArray);
            Collections.addAll(stopWords, "abcdefghijklmnopqrstuvwxyz".split(""));
        }

        public boolean isStopWord(String word) {
            return stopWords.contains(word);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Sample Usage: java Iterators ./../pride-and-prejudice.txt");
            System.exit(1);
        }

        CharacterGenerator characterGenerator = new CharacterGenerator(args[0]);
        WordIterator wordIterator = new WordIterator(characterGenerator);
        StopWords stopWords = new StopWords();
        Map<String, Integer> wordFreqs = new HashMap<>();
        int i = 1;

        while (wordIterator.hasNext()) {
            String word = wordIterator.next();
            if (word == null) {
                break; // End of file
            }

            if (!stopWords.isStopWord(word)) {
                wordFreqs.put(word, wordFreqs.getOrDefault(word, 0) + 1);
            }

            if (i % 5000 == 0) {
                printWordFreqs(wordFreqs);
            }
            i++;
        }

        wordIterator.close();
        printWordFreqs(wordFreqs);
    }

    private static void printWordFreqs(Map<String, Integer> wordFreqs) {
        Map<String, Integer> sortedWordFreqs = new TreeMap<>((o1, o2) -> {
            int freqComparison = Integer.compare(wordFreqs.get(o2), wordFreqs.get(o1));
            if (freqComparison != 0) {
                return freqComparison;
            }
            return o1.compareTo(o2);
        });

        sortedWordFreqs.putAll(wordFreqs);

        System.out.println("----------------------------");
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedWordFreqs.entrySet()) {
            if (count >= 25) break;
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
            count++;
			if( count >= 25 ) break;
        }
    }
}

