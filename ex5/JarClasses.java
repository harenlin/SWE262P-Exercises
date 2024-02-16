import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.TreeMap;
import java.util.Map;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

public class JarClasses {
	private static String retrieve_class_record(Class<?> cur_class){
		int publicMethods = 0;
		int privateMethods = 0;
		int protectedMethods = 0;
		int staticMethods = 0;
		int declaredFields = 0;

		for(Method method : cur_class.getDeclaredMethods()){
			if( java.lang.reflect.Modifier.isStatic(method.getModifiers()) ) staticMethods++;

			if( java.lang.reflect.Modifier.isPublic(method.getModifiers()) ) publicMethods++;
			else if ( java.lang.reflect.Modifier.isProtected(method.getModifiers()) ) protectedMethods++;
			else if ( java.lang.reflect.Modifier.isPrivate(method.getModifiers()) ) privateMethods++;
		}

		for(Field field : cur_class.getDeclaredFields()) declaredFields++;

		String record = publicMethods + ";" + privateMethods + ";" + protectedMethods + ";" + staticMethods + ";" + declaredFields;
		return record;
	}


	private static void printResults(TreeMap<String, String> recordMap){
		for(Map.Entry<String, String> entry : recordMap.entrySet()){
			System.out.println("----------" + entry.getKey() + "----------");
			String[] records = entry.getValue().split(";");
			System.out.println("Public methods: " + Integer.parseInt(records[0]));
			System.out.println("Private methods: " + Integer.parseInt(records[1]));
			System.out.println("Protected methods: " + Integer.parseInt(records[2]));
			System.out.println("Static methods: " + Integer.parseInt(records[3]));
			System.out.println("Fields: " + Integer.parseInt(records[4]));
		}
	} 


	public static void main(String[] args) {
		if( args.length != 1){
			System.err.println("Usage: java JarClass </path/to/jar_file>");
			return;
		}

		String jar_file_path = args[0];
		File jar_file = new File(jar_file_path);

		TreeMap<String, String> recordMap = new TreeMap<>();

		try {
			URL jar_url = jar_file.toURI().toURL();
			URLClassLoader classLoader = new URLClassLoader(new URL[]{jar_url});

			try(JarFile cur_jar_file = new JarFile(jar_file_path)){
				Enumeration<JarEntry> entries = cur_jar_file.entries();
				while( entries.hasMoreElements() ){
					JarEntry cur_entry = entries.nextElement();
					if( cur_entry.getName().endsWith(".class") ){
						String cur_class_name = cur_entry.getName().replace("/", ".").replaceAll("\\.class$", "");
						// System.out.println(cur_class_name);
						Class<?> cur_loaded_class = classLoader.loadClass(cur_class_name);
						String res = retrieve_class_record(cur_loaded_class);
						// System.out.println(res);
						recordMap.put(cur_class_name, res);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			printResults(recordMap);
		} catch (MalformedURLException e) {
			e.printStackTrace(); 
		}
	}
}

