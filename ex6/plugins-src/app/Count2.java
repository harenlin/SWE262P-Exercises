import java.util.*;

public class Count2 implements ITFCount {
    @Override
    public List<Map.Entry<String, Integer>> count(List<String> words) {
		// Make List<String> as a map
		Map<String, Integer> wf = new TreeMap<>();
		for(String w : words) {
			// wf.put(w, wf.getOrDefault(w, 0) + 1);
			String firstLetter = null;
			if( w.length() == 0 )
				continue;
			else if ( w.length() == 1 )
				firstLetter = w;
			else 
			   	firstLetter = w.substring(0, 1).toLowerCase();

			if( Character.isLetter(firstLetter.charAt(0)) )
            	wf.put(firstLetter, wf.getOrDefault(firstLetter, 0) + 1);
		}
		// Get the 26 entries
		List<Map.Entry<String, Integer>> wf_list = new ArrayList<>(wf.entrySet());
		return wf_list;
	}
}
