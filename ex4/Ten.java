import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.regex.Pattern;
import java.util.function.Function;

class TFTheOne {
	private Object value;

	public TFTheOne(Object v) {
		this.value = v;
	}

	public TFTheOne bind(Function<Object, Object> func) {
		this.value = func.apply(this.value);
		return this;
	}

	public void printme() {
		System.out.println(this.value);
	}
}


public class Ten {
	public static Function<Object, Object> read_file = (object) -> {
		Path path = Paths.get((String) object);
		try {
			byte[] data = Files.readAllBytes(path);
			return new String(data, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	};


	public static Function<Object, Object> filter_chars = (object) -> {
		Pattern pattern = Pattern.compile("[\\W_]+");
		return pattern.matcher((String) object).replaceAll(" ");
	};


	public static Function<Object, Object> normalize = (object) -> {
		return ((String) object).toLowerCase();	
	};


	public static Function<Object, Object> scan = (object) -> {
		return Arrays.asList(((String) object).split("\\s+"));	
	};


	@SuppressWarnings("unchecked")
		public static Function<Object, Object> remove_stop_words = (object) -> {
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
			List<String> words = new ArrayList<>();
			for(String w : (List<String>) object){
				if( !stop_words.contains(w) ) words.add((String) w);
			}
			return words;
		};


	@SuppressWarnings("unchecked")
		public static Function<Object, Object> frequencies = (object) -> {
			Map<String, Integer> wf = new HashMap<>();
			for(String w : (List<String>) object) {
				wf.put(w, wf.getOrDefault(w, 0) + 1);
			}
			return wf;
		};


	@SuppressWarnings("unchecked")
		public static Function<Object, Object> sort_map = (object) -> {
			List<Map.Entry<String, Integer>> wf_list = new ArrayList<>(((Map<String, Integer>) object).entrySet());
			wf_list.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
			return wf_list;
		};


	@SuppressWarnings("unchecked")
		public static Function<Object, Object> top25_freqs = (object) -> {
			int map_size = ((List<Map.Entry<String, Integer>>) object).size();
			StringBuilder top25 = new StringBuilder();
			for(int i = 0; i < 25 && i < map_size; i++) {
				Map.Entry<String, Integer> word_freq_pair = ((List<Map.Entry<String, Integer>>) object).get(i);
				top25.append(word_freq_pair.getKey()).append("  -  ").append(word_freq_pair.getValue()).append("\n");
			} 
			String res = top25.toString();
			return res.substring(0, res.length() - 1);
		};


	public static void main(String[] args) {
		if( args.length < 1 ){
			System.err.println("java Ten <path_to_file>");
		}
		new TFTheOne(args[0])
			.bind(read_file)
			.bind(filter_chars)
			.bind(normalize)
			.bind(scan)
			.bind(remove_stop_words)
			.bind(frequencies)
			.bind(sort_map)
			.bind(top25_freqs)
			.printme();
		// Ten ten = new Ten(); // Create an instance of Ten
		// new TFTheOne(args[0]).bind(v -> ten.read_file((String) v));
	}
}
