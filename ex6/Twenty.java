/* 
#!/usr/bin/env python
import sys, configparser, importlib.machinery

def load_plugins():
    config = configparser.ConfigParser()
    config.read("config.ini")
    words_plugin = config.get("Plugins", "words")
    frequencies_plugin = config.get("Plugins", "frequencies")
    global tfwords, tffreqs
    tfwords = importlib.machinery.SourcelessFileLoader('tfwords', words_plugin).load_module()
    tffreqs = importlib.machinery.SourcelessFileLoader('tffreqs', frequencies_plugin).load_module()

load_plugins()
word_freqs = tffreqs.top25(tfwords.extract_words(sys.argv[1]))

for (w, c) in word_freqs:
    print(w, '-', c)
*/

/* [SPEC]
 * The problem is decomposed using some form of abstraction (procedures, functions, objects, etc.)
 * All or some of those abstractions are physically encapsulated into their own, usually pre-compiled, packages. Main program and each of the packages are compiled independently. These packages are loaded dynamically by the main program, usually in the beginning (but not necessarily).
 * Main program uses functions/objects from the dynamically-loaded packages, without knowing which exact implementations will be used. New implementations can be used without having to adapt or recompile the main program.
 * External specification of which packages to load. This can be done by a configuration file, path conventions, user input or other mechanisms for external specification of code to be linked at run time.
 * 
 *
 * =======================================================================
 *
 * Provide 2 plugins for extracting words: 
 * 	one should implement the "normal" extraction we have been using so far; 
 * 	the second one should extract only non-stop words with z.
 *
 * Provide 2 plugins for counting words: 
 * 	one should implement the "normal" counting we have been using so far; 
 * 	the second one should count words based on their first letters, 
 * 	so words starting with 'a', words starting with 'b', etc.
 *
 * */

public class Twenty {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
		if( args.length < 1 ){
			System.err.println("Please specify your /path/to/file.");
			System.exit(1);
		}

		// 1) Parse config file
		// 2) Get the plugin for words
		// 3) Get the plugin for freqs => 1~3 wrapped in load_plugins
		// 4) Get word_freqs map by using plugins
		// 5) Print Top25 out
		
		load_plugins();
		var word_freqs = tffreqs.top25(tfwords.extract_words(args[0]));
        word_freqs.forEach(entry -> System.out.println(entry.getKey() + "  -  " + entry.getValue()));


    }
}

