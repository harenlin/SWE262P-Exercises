import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Iterators {

    static class characters implements Iterator<Character> {
		private String fileName;
        private final BufferedReader reader;
        private char nextChar;

        public characters(String fileName) throws IOException {
			this.fileName = fileName;
            this.reader = new BufferedReader(new FileReader(this.fileName));
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
        private final characters characterGenerator;
        private boolean startChar;

        public WordIterator(characters characterGenerator) {
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
    }
}
