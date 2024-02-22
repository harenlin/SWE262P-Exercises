import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Streams {
    public static void main(String[] args) throws IOException {
        Map<String, Integer> stopWords = 
			Arrays.stream(Files.readString(Paths.get("./../stop_words.txt")).split(","))
                .collect(Collectors.toMap(String::trim, s -> 1));
		"abcdefghijklmnopqrstuvwxyz".chars()
        	.mapToObj(c -> String.valueOf((char) c))
        	.forEach(word -> stopWords.put(word, 1));

        String fileContent = Files.readString(Paths.get(args[0]));
        Pattern pattern = Pattern.compile("[a-z]{2,}");
        Map<String, Integer> counts = Arrays.stream(pattern.matcher(fileContent.toLowerCase()).results()
                .map(matchResult -> matchResult.group())
                .toArray(String[]::new))
                .filter(w -> !stopWords.containsKey(w))
                .collect(Collectors.toMap(w -> w, w -> 1, Integer::sum));

        counts.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
                .limit(25)
                .forEach(entry -> System.out.printf("%s  -  %d\n", entry.getKey(), entry.getValue()));
    }
}

