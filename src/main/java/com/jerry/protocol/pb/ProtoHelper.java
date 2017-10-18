package com.jerry.protocol.pb;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.protobuf.GeneratedMessageV3;

/**
 * proto file pre-treated and generate java file from it with protoc tool
 * @author jerrywbchen
 *
 */
public class ProtoHelper {
	
	private boolean update = false;
	private File protoFile = null;
	private String version = "proto2";
	private String outerClassName = "";
	private String packageName = "";
	
	/**
	 * Constructor
	 * @param _update: update class file or java file even it exists
	 * @param pfile: target proto file object
	 * @throws Exception
	 */
	public ProtoHelper(boolean _update, File pfile) throws Exception{
		this.update = _update;
		this.protoFile = pfile;
		String protoName = pfile.getName().replace(".proto", "");
		protoName = protoName.substring(0, 1).toUpperCase() + protoName.substring(1);
		this.outerClassName = protoName + "OuterClass";
		List<String> imports = getImports(pfile);
		for(String _import : imports){
			File import_file = new File(pfile.getParentFile().getAbsolutePath() + "/" + _import);
			new ProtoHelper(_update, import_file);
		}
		this.iniProtoFile();
		this.loadOuterClass();
	}

	@SuppressWarnings("unchecked")
	public Class<GeneratedMessageV3> loadMessageClass(String messageName) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, IOException{
		JavaHelper javahelper = new JavaHelper(update);
		String classname = this.packageName.isEmpty() ? this.outerClassName + "$" + messageName : this.packageName + "." + this.outerClassName + "$" + messageName;
		return (Class<GeneratedMessageV3>) javahelper.loadClass(classname);
	}
	
	public static void close(){
		JavaHelper.close();
	}
	
	/**
	 * Initial proto file before load it. There will be some minor changes in your proto file
	 * if it has no java_outer_classname declaration
	 * if it has not version declaration (use syntax 2 by default)
	 * There will be a warning if it has no package declaration. please add a package if translation fails
	 * for this reason
	 * @throws Exception
	 */
	private void iniProtoFile() throws Exception{
		boolean has_class_name = false;
		boolean has_version = false;
		boolean has_package = false;
		String class_name_line = "option java_outer_classname=\"" + outerClassName + "\";";
		String version_line = "syntax=\"" + version + "\";";
		StringBuilder sb = new StringBuilder();
		List<String> lines = FileUtils.readLines(protoFile, "UTF-8");
		for(String line : lines){
			if(line.trim().startsWith("syntax")){
				String[] arr = line.split("\"");
				if(arr.length > 2){
					this.setVersion(arr[1]);
					sb.append(line + "\n");
					has_version = true;
				}
			}else if(line.trim().contains("java_outer_classname")){
				String[] arr = line.split("\"");
				if(arr.length > 2){
					this.setOuterClassName(arr[1]);
				}
				sb.append(line + "\n");
				has_class_name = true;
			}else if(line.trim().startsWith("package")){
				String package1 = line.replace("package", "").replace(";", "").trim();
				this.setPackageName(package1);
				has_package = true;
				sb.append(line + "\n");
			}else{
				sb.append(line + "\n");
			}
		}
		if(!has_package)System.out.println("Package declaration not found in " + protoFile.getAbsolutePath() 
				+ ",it may cause problems as a class in a package cannot access class in default package!");
		if(!has_class_name)sb.insert(0, class_name_line + "\n");
		if(!has_version)sb.insert(0, version_line + "\n");
		FileUtils.writeStringToFile(protoFile, sb.toString(), "UTF-8");
	}
	
	/**
	 * Find imported proto files, imported file should be loaded also
	 * @param pfile
	 * @return
	 * @throws IOException
	 */
	private List<String> getImports(File pfile) throws IOException {
		List<String> imports = new LinkedList<String>();
		List<String> lines = FileUtils.readLines(pfile, "UTF-8");
		for(String line : lines){
			if(line.trim().startsWith("import")){
				String[] arr = line.split("\"");
				if(arr.length > 2){
					imports.add(arr[1]);
				}
			}
		}
		return imports;
	}
	
	private void loadOuterClass() throws Exception{
		JavaHelper javahelper = new JavaHelper(update);
		File javaFile = this.generateJavaFile();
		javahelper.compileJavaCode(javaFile);
		String classname = packageName.isEmpty() ? this.outerClassName : packageName + "." + this.outerClassName;
		javahelper.loadClass(classname);
	}
		
	/**
	 * Generate Message class with protoc tool
	 * @return Generated .java file object
	 * @throws Exception
	 */
	private File generateJavaFile() throws Exception{
		String java_path = Constants.GENERATED_JAVA_PATH;
		File generated_java_path = new File(java_path);
		if(generated_java_path.exists() == false)generated_java_path.mkdirs();
		String java_file_name = this.getOuterClassName() + ".java";
		String package_name = this.packageName;
		File java_file = new File(java_path + "/" + package_name.replace(".", "/") + "/" + java_file_name);
		//Use previous java file if update is not needed
		if( update== false && java_file.exists())return java_file;
		
		String tools_path = Constants.PROTOC_PATH;
		String cmd = tools_path + "/protoc.exe --java_out " + java_path + 
				" -I " + protoFile.getParentFile().getAbsolutePath() + " " 
				+ protoFile.getAbsolutePath(); 
		if(Utility.isLinux()){
			cmd = tools_path + "/protoc --java_out "+ java_path + 
					" -I " + protoFile.getParentFile().getAbsolutePath() + " " 
					+ protoFile.getAbsolutePath(); 
			cmd = "chmod a+x " + tools_path + "/protoc;" + cmd;
		}
		System.out.println(cmd);
		Utility.runLocalCmd(cmd);
		if(java_file.exists()){
			return java_file;
		}else{
			throw new Exception(java_file_name + "java file not found");
		}
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getOuterClassName() {
		return outerClassName;
	}

	public void setOuterClassName(String outerClassName) {
		this.outerClassName = outerClassName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public File getProtoFile() {
		return protoFile;
	}

	public void setProtoFile(File protoFile) {
		this.protoFile = protoFile;
	}
}
