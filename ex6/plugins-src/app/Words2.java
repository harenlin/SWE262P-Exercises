import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Words2 implements ITFWords {
    @Override
    public List<String> extract(String path_to_file) {
		// Read-in txt file
		StringBuilder strData = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path_to_file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                strData.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		// Do some pre-processing
		Pattern pattern = Pattern.compile("[\\W_]+");
        String[] wordArray = pattern.matcher(strData.toString().toLowerCase()).replaceAll(" ").split("\\s+");
        List<String> wordList = Arrays.asList(wordArray);
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
		// Filtering
		List<String> words = new ArrayList<>();
		for(String w : wordList){
			if( !stop_words.contains(w) && w.contains("z") ) words.add(w);
		}
		return words; 
    }
}
