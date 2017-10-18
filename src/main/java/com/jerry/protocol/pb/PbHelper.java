package com.jerry.protocol.pb;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;

/**
 * Translate a pb file to a txt file or vice versa.
 * @author jerrywbchen
 *
 */
public class PbHelper {
	Boolean _update = true;
	
	public PbHelper(Boolean update){
		_update = update;
	}
	
	/**
	 * Translate pb file to txt file
	 * @param pbFile: pb file path
	 * @param protoFile: proto file path
	 * @param message: target message used in the pb file
	 * @return the content of the pb file in readable txt format
	 * @throws Exception
	 */
	public String pb2txt(String pbFile, String protoFile, String message) throws Exception{
		File proto_file = new File(protoFile);
		
		//Ini message class
		ProtoHelper protohelper = new ProtoHelper(_update, proto_file);	
		Class<GeneratedMessageV3> message_class = protohelper.loadMessageClass(message);
		
		//change pb to txt
		File pb_file = new File(pbFile);
		String content = pb2txt(message_class, pb_file);
		File txtFile = new File(pbFile.replace(".pb", ".txt"));
		FileUtils.writeStringToFile(txtFile, content, "UTF-8");
		return content;		
	}
	
	/**
	 * Translate pb to txt file
	 * @param pb_bytes: pb file content in byte array
	 * @param protoFile: proto file path
	 * @param message: target message used in the pb file
	 * @return the content of the pb file in readable txt format
	 * @throws Exception
	 */
	public String pb2txt(byte[] pb_bytes, String protoFile, String message) throws Exception{
		File proto_file = new File(protoFile);
		
		//Ini message class
		ProtoHelper protohelper = new ProtoHelper(_update, proto_file);	
		Class<GeneratedMessageV3> message_class = protohelper.loadMessageClass(message);
		
		//change pb to txt
		String content = pb2txt(message_class, pb_bytes);
		return content;		
	}
	
	/**
	 * Translate txt file to pb file
	 * @param txtFileName: txt file path
	 * @param protoFileName: proto file path
	 * @param message: target message used in the txt file
	 * @return pb file object
	 * @throws Exception
	 */
	public File txt2pb(String txtFileName, String protoFileName, String message) throws Exception{
		File proto_file = new File(protoFileName);
		
		//Ini message class
		ProtoHelper protohelper = new ProtoHelper(_update, proto_file);	
		Class<GeneratedMessageV3> message_class = protohelper.loadMessageClass(message);
		
		//change pb to txt
		File txtFile = new File(txtFileName);
		File pbFile = txt2pb(message_class, txtFile);
		return pbFile;
	}	
	
	/**
	 * Translate txt file to pb file
	 * @param txtFileName: txt file path
	 * @param protoFileName: proto file path
	 * @param message: target message used in the txt file
	 * @return Message object
	 * @throws Exception
	 */
	public Message txt2Builder(String txt_file, String protoFileName, String message) throws Exception{
		File proto_file = new File(protoFileName);
		
		//Ini message class
		ProtoHelper protohelper = new ProtoHelper(_update, proto_file);	
		Class<GeneratedMessageV3> message_class = protohelper.loadMessageClass(message);
		
		//change pb to txt
		File txtFile = new File(txt_file);
		return txt2builder(message_class, FileUtils.readFileToString(txtFile, "UTF-8"));		
	}
	
	public void close(){
		ProtoHelper.close();
	}
	
	private String pb2txt(Class<GeneratedMessageV3> clazz, File pbFile) throws Exception{	
		try{
			FileInputStream fis_student = new FileInputStream(pbFile);
			Method method = clazz.getMethod("parseFrom", InputStream.class);
			GeneratedMessageV3 s3 = (GeneratedMessageV3) method.invoke(clazz, fis_student);
			fis_student.close();
			return s3.toString();
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error when decode pb");
		}
	}
	
	private String pb2txt(Class<GeneratedMessageV3> clazz, byte[] pb_bytes) throws Exception{	
		try{
			Method method = clazz.getMethod("parseFrom", byte[].class);
			GeneratedMessageV3 s3 = (GeneratedMessageV3) method.invoke(clazz, pb_bytes);
			return s3.toString();
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error when decode pb");
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private File txt2pb(Class<GeneratedMessageV3> message_class, File txtFile) throws Exception{	
		try{
			String content = FileUtils.readFileToString(txtFile, "UTF-8");
			Method method = message_class.getMethod("newBuilder");
			GeneratedMessageV3.Builder builder = (com.google.protobuf.GeneratedMessageV3.Builder) method.invoke(null);
			TextFormat.merge(content, builder);	
			String file_name = txtFile.getAbsolutePath().replace(".txt", ".pb");
			FileOutputStream new_pbFile = new FileOutputStream(file_name);
			builder.build().writeTo(new_pbFile);
			new_pbFile.close();
			return new File(file_name);
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error when encode pb");
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	private Message txt2builder(Class<GeneratedMessageV3> message_class, String content) throws Exception{	
		try{
			Method method = message_class.getMethod("newBuilder");
			GeneratedMessageV3.Builder builder = (com.google.protobuf.GeneratedMessageV3.Builder) method.invoke(null);
			TextFormat.merge(content, builder);	
			return builder.build();
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error when encode pb");
		}
	}
}
