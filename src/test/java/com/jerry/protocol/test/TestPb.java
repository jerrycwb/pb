package com.jerry.protocol.test;

import java.io.File;

import org.testng.annotations.Test;

import com.jerry.protocol.pb.PbHelper;
import com.jerry.protocol.pb.Utility;

public class TestPb {

	@Test
	public void TestStudent() throws Exception{
		String path = Utility.getProjectPath();
		String proto_file = path + "/src/test/resources/protos/student.proto";
		String message = "Student";
		
		//Translate pb to txt
		String pb_file = path + "/src/test/resources/pbs/student.pb";
		PbHelper helper = new PbHelper(false);
		String txtMessage = helper.pb2txt(pb_file, proto_file, message);
		System.out.println(txtMessage);
		
		//Translate txt to pb
		String txt_file = path + "/src/test/resources/txts/student.txt";
		File pbFile = helper.txt2pb(txt_file, proto_file, message);
		System.out.println(pbFile.getAbsolutePath());
	}
	
	@Test
	public void TestTeacher() throws Exception{
		String path = Utility.getProjectPath();
		String proto_file = path + "/src/test/resources/protos/teacher.proto";
		String message = "Teacher";
		
		//Translate pb to txt
		String pb_file = path + "/src/test/resources/pbs/teacher.pb";
		PbHelper helper = new PbHelper(false);
		String txtMessage = helper.pb2txt(pb_file, proto_file, message);
		System.out.println(txtMessage);
		
		//Translate txt to pb
		String txt_file = path + "/src/test/resources/txts/teacher.txt";
		File pbFile = helper.txt2pb(txt_file, proto_file, message);
		System.out.println(pbFile.getAbsolutePath());
	}
}
