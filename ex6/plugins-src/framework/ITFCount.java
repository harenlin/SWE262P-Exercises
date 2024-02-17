import java.util.List;
import java.util.Map;

public interface ITFCount {
	List<Map.Entry<String, Integer>> count(List<String> words);
}
