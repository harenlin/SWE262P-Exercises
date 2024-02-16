import java.util.*;

public class Count1 implements ITFCount {
    @Override
    public List<Map.Entry<String, Integer>> count(List<String> words) {
		// Make List<String> as a map
		Map<String, Integer> wf = new HashMap<>();
		for(String w : words) {
			wf.put(w, wf.getOrDefault(w, 0) + 1);
		}
		// Get the top 25 entries
		List<Map.Entry<String, Integer>> wf_list = new ArrayList<>(wf.entrySet());
		wf_list.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
		return wf_list.subList(0, 25);
	}
}
