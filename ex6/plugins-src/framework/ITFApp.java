import java.util.List;
import java.util.Map;

public interface ITFApp {
    List<String> extract(String pathString);
    List<Map. Entry<String, Integer>> count(List<String> words);
}
