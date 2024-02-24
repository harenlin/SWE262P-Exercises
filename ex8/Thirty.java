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
import java.util.regex.Pattern;

// https://github.com/crista/exercises-in-programming-style/blob/master/30-dataspaces/tf-30.py

public class Thirty {
	
	public static void main(String[] args) {
		System.out.println("Hello World!");

		// Read-in stopwords
		List<String> stop_words = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader("./../stop_words.txt"))) {
			String line = br.readLine();
			if( line != null ) {
				stop_words.addAll(Arrays.asList(line.split(",")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		stop_words.addAll(Arrays.asList("a", "b", 
			"c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", 
			"o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));

	}
}

