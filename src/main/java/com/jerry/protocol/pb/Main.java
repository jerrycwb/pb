package com.jerry.protocol.pb;

public class Main {

	public static void main(String[] args) throws Exception {
		if(args.length < 3){
			System.out.println("Uasge: java -jar pb.jar pbfile|txfile protofile message [true|false]");
			System.out.println("Parameters：");
			System.out.println("        pbfile|txtfile：Required.File to be translated");
			System.out.println("             protofile：Required.Proto file that describes the message in the file above");
			System.out.println("               message： Required.Message name to be translated");
			System.out.println("use_existing_java_file：Optional.true or false, default is false");
		}else{
			PbHelper helper = null;
			try{
				String pb_txt_file = args[0].trim();
				String proto_file = args[1].trim();
				String message = args[2].trim();
				Boolean update = true;
				if(args.length >= 4){
					String using_exists = args[3].trim();
					if(using_exists.equals("true")){
						update = false;
					}
				}
				helper = new PbHelper(update);
				if(pb_txt_file.trim().endsWith(".pb")){
					System.out.println(helper.pb2txt(pb_txt_file, proto_file, message));				
				}else if(pb_txt_file.trim().endsWith(".txt")){
					System.out.println(helper.txt2pb(pb_txt_file, proto_file, message));
				}else{
					System.out.println("Translated file supports .txt or .pb files only");
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				helper.close();
			}
		}
	}
}
