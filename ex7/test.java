import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class test {

	public static class CharactersSpliterator implements Spliterator<Character> {
		private BufferedReader reader;

		public CharactersSpliterator(String filename) throws IOException {
			this.reader = new BufferedReader(new FileReader(filename));
		}

		public Stream<Character> stream() {
			return StreamSupport.stream(this, false);
		}

		@Override
			public boolean tryAdvance(Consumer<? super Character> action) {
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

        public AllWordsSpliterator(String filename) throws IOException {
            this.charactersSpliterator = new CharactersSpliterator(filename);
        }

        public Stream<String> stream() {
			return StreamSupport.stream(this, false);
		}

        @Override
        public boolean tryAdvance(Consumer<? super String> action) {
            boolean[] startChar = {true};
            // boolean startChar = true; --> 
            // local variables referenced from a lambda expression must be final or effectively final
            StringBuilder wordBuilder = new StringBuilder();

            while (charactersSpliterator.tryAdvance(c -> {
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
            }));

            if( wordBuilder.length() != 0 ){ // Check for the last word
                this.currentWord = wordBuilder.toString();
                wordBuilder.setLength(0);
                action.accept(this.currentWord);
                return true;
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

	public static void main(String[] args) {
		AllWordsSpliterator spliterator;
		try {
			spliterator = new AllWordsSpliterator(args[0]);
			spliterator.stream().forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

