package com.jerry.protocol.pb;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * compile java code and load java class dynamically
 * @author jerrywbchen
 *
 */
public class JavaHelper {
	private static boolean update = false;
	private static URLClassLoader classloader = null;
	
	public JavaHelper(boolean _update){
		update = _update;
	}

	public Class<?> loadClass(String classname) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, IOException {
		if(classloader == null){
			File file = new File(Constants.GENERATED_JAVA_PATH);
			classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
			add.setAccessible(true);
			add.invoke(classloader, new Object[]{file.toURI().toURL()});			
		}
		Class<?> clazz = classloader.loadClass(classname);
		return clazz;
	}

	public File compileJavaCode(File javaFile) throws Exception {
		String path = javaFile.getParentFile().getAbsolutePath();
		String name = javaFile.getName();
		String className = javaFile.getAbsolutePath().replace(".java", ".class");

		File classFile = new File(className);

		//Return previous class file if updated is not required
		if(update == false && classFile.exists())return classFile;
		
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		String cp = "";
		String codePath = JavaHelper.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		String connector = ";";
		if(Utility.isLinux())connector = ":";
		if(codePath.endsWith("jar")){
			 cp = codePath + connector + Constants.GENERATED_JAVA_PATH;
		}else{
			cp = System.getenv("USERPROFILE") + "\\.m2\\repository\\com\\google\\protobuf\\protobuf-java\\3.1.0\\protobuf-java-3.1.0.jar;" + Constants.GENERATED_JAVA_PATH;
		}	
		System.out.println("class_path:" + cp);
		int compilationResult = javac.run(null, null, null, "-d", Constants.GENERATED_JAVA_PATH, "-encoding","utf-8","-cp",cp, path + "/" + name);
		if(compilationResult != 0){
			throw new Exception("Cannot compile java file " + javaFile.getAbsolutePath());
		}
		if(classFile.exists()){
			return classFile;
		}else{
			throw new Exception(className + " not found!");
		}
	}
	
	public static void close(){
		try {
			if(classloader != null)classloader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		classloader = null;
	}
}
