import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.io.IOException;
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


	public static void main(String[] args) {
		CharactersSpliterator spliterator;
		try {
			spliterator = new CharactersSpliterator(args[0]);
			spliterator.stream().forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

