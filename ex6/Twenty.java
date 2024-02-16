/* 
def load_plugins():
    config = configparser.ConfigParser()
    config.read("config.ini")
    words_plugin = config.get("Plugins", "words")
    frequencies_plugin = config.get("Plugins", "frequencies")
    global tfwords, tffreqs
    tfwords = importlib.machinery.SourcelessFileLoader('tfwords', words_plugin).load_module()
    tffreqs = importlib.machinery.SourcelessFileLoader('tffreqs', frequencies_plugin).load_module()
*/

/* [SPEC]
 * Provide 2 plugins for extracting words: 
 * 	one should implement the "normal" extraction we have been using so far; 
 * 	the second one should extract only non-stop words with z.
 *
 * Provide 2 plugins for counting words: 
 * 	one should implement the "normal" counting we have been using so far; 
 * 	the second one should count words based on their first letters, 
 * 	so words starting with 'a', words starting with 'b', etc.
 * */

import java.io.FileInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.TreeMap;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

public class Twenty {

	private static String CONFIG_PATH = "./config.properties";

	private static ITFWords Extractor;
	private static ITFCount Counter;
	
	public static void load_plugins() {
    	Properties propt = new Properties();
		try {
			propt.load(new FileInputStream(CONFIG_PATH));
		} catch(Exception e){
			e.printStackTrace();
		} 
		
		String wordsClassName = propt.getProperty("words");
        String wordsJarName = "plugins/" + wordsClassName + ".jar";
        URL wordsJarURL = null;
		try {
			wordsJarURL = new File(wordsJarName).toURI().toURL();
		} catch(Exception e){
			e.printStackTrace();
		} 
		// System.out.println("---" + wordsClassName + " " + wordsJarName);

		String countClassName = propt.getProperty("count");
        String countJarName = "plugins/" + countClassName + ".jar";
        URL countJarURL = null;
		try {
			countJarURL = new File(countJarName).toURI().toURL();
		} catch(Exception e){
			e.printStackTrace();
		} 
		// System.out.println("---" + countClassName + " " + countJarName);

		URLClassLoader classLoader1 = new URLClassLoader(new URL[]{wordsJarURL});
		URLClassLoader classLoader2 = new URLClassLoader(new URL[]{countJarURL});

		// System.out.println(wordsJarURL);
		// System.out.println(countJarURL);
		
		try {
			Extractor = (ITFWords) classLoader1.loadClass(wordsClassName).getDeclaredConstructor().newInstance();     
			Counter = (ITFCount) classLoader2.loadClass(countClassName).getDeclaredConstructor().newInstance();
		} catch(Exception e){
			e.printStackTrace();
		} 

	}

    public static void main(String[] args) {
		if( args.length < 1 ){
			System.err.println("Please specify your /path/to/file.");
			System.exit(1);
		} 
		
		load_plugins();
		List<Map. Entry<String, Integer>> word_freqs = Counter.count(Extractor.extract(args[0]));
		for(Map.Entry<String, Integer> entry : word_freqs) {
            System.out.println(entry.getKey() + "  -  " + entry.getValue());
        }
    }
}

