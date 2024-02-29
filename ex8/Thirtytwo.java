import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Functions for map-reduce
public class Thirtytwo {

	private static List<String> partition(String data_str, int nlines) {
		/* Partitions the input data_str (a big string) into chunks of nlines. */
		List<String> lines = Arrays.asList(data_str.split("\n"));
		List<String> partitions = new ArrayList<>();
		for(int i = 0; i < lines.size(); i += nlines){
			int endIdx = Math.min(i + nlines, lines.size());
			partitions.add(String.join("\n", lines.subList(i, endIdx)));
		}
		return partitions;
	}


	private static List<Map.Entry<String, Integer>> splitWords(String data_str) throws IOException {
		/*	Takes a string, returns a list of pairs (word, 1),
			one for each word in the input, so [(w1, 1), (w2, 1), ..., (wn, 1)] */

		// In Java, we cannot create functions inside a function!

		// for _scan
		Pattern pattern = Pattern.compile("[\\W_]+");

		// for _remove_stop_words	
		Set<String> stopwords = new HashSet<>();
		try(BufferedReader stopwordsReader = new BufferedReader(new FileReader("./../stop_words.txt"))){
			String[] words = stopwordsReader.readLine().split(",");
			for(String stopword : words){
				stopwords.add(stopword.trim());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		for(char c = 'a'; c <= 'z'; c++) stopwords.add(String.valueOf(c));

		// The actual work of the mapper
		List<Map.Entry<String, Integer>> result = new ArrayList<>();
		List<String> words = Arrays.stream(pattern.split(data_str.toLowerCase())).filter(word -> !stopwords.contains(word)).collect(Collectors.toList());
		for(String word : words){
			result.add(new AbstractMap.SimpleEntry<>(word, 1));
		}
		return result;
	}


	private static Map<String, List<Map.Entry<String, Integer>>> regroup(List<List<Map.Entry<String, Integer>>> pairs_list) {
		/* 
		   Takes a list of lists of pairs of the form
		   [[(w1, 1), (w2, 1), ..., (wn, 1)],
		    [(w1, 1), (w2, 1), ..., (wn, 1)],
		    ...]
		   and returns a dictionary mapping each unique word to the
		   corresponding list of pairs, so
		   {w1 : [(w1, 1), (w1, 1)...],
			w2 : [(w2, 1), (w2, 1)...],
			...}
		 */
		Map<String, List<Map.Entry<String, Integer>>> mapping = new HashMap<>();
		for(List<Map.Entry<String, Integer>> pairs : pairs_list){
			for(Map.Entry<String, Integer> p : pairs){
				if( mapping.containsKey(p.getKey()) ){
					mapping.get(p.getKey()).add(p);
				} else {
					List<Map.Entry<String, Integer>> newList = new ArrayList<>();
					newList.add(p);
					mapping.put(p.getKey(), newList);
				}
			}
		}
		return mapping;
	}


	private static Map.Entry<String, Integer> countWords(Map.Entry<String, List<Map.Entry<String, Integer>>> entry) {
		/* 	Takes a mapping of the form (word, [(word, 1), (word, 1)...)])
			and returns a pair (word, frequency), where frequency is the sum of all the reported occurrences. */
		// System.out.println(entry.getValue().stream().map(Map.Entry::getValue).reduce(Integer::sum).orElse(0));
		int frequency = entry.getValue().stream().map(Map.Entry::getValue).reduce(Integer::sum).orElse(0);
		// 					 .orElseThrow(() -> new IllegalStateException("No value present"));
		return new AbstractMap.SimpleEntry<>(entry.getKey(), frequency);
	}


	// Auxiliary functions - (1)
	private static String read_file(String path_to_file) throws IOException {
		try {
			byte[] data = Files.readAllBytes(Paths.get((String) path_to_file));
			return new String(data, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}


	// Auxiliary functions - (2)
	private static List<Map.Entry<String, Integer>> sort_freqs(List<Map.Entry<String, Integer>> word_freq) {
		return word_freq.stream()
			.sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed())
			.collect(Collectors.toList());
	}


	// The main function
	public static void main(String[] args) throws IOException {
		List<List<Map.Entry<String, Integer>>> splits = partition(read_file(args[0]), 200).stream()
                                                        .map(data_str -> {
                                                            try {
                                                                return splitWords(data_str);
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                                return Collections.<Map.Entry<String, Integer>>emptyList();
                                                            }
                                                        }).collect(Collectors.toList());
		Map<String, List<Map.Entry<String, Integer>>> splits_per_word = regroup(splits);
		List<Map.Entry<String, Integer>> word_freqs = sort_freqs(splits_per_word.entrySet().stream().map(Thirtytwo::countWords).collect(Collectors.toList()));
		word_freqs.stream().limit(25).forEach(entry -> System.out.println(entry.getKey() + "  -  " + entry.getValue())); 
	}
}

