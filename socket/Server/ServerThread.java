package socket.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import File.FileManagement;

/** 
 * 该类为多线程类，用于服务端 
 */  
public class ServerThread implements Runnable {  
	public final static int bytesize = 2048;
	//密钥
	private static final int numOfEncAndDec = 20164277;
    private Socket client = null;  
    public ServerThread(Socket client){  
        this.client = client;  
    }  

    @Override  
    public void run() {  
        try{  
        	File directory = new File("");//设定为当前文件夹
    		String SystemPath = directory.getAbsolutePath();
            //获取Socket的输出流，用来向客户端发送数据  
            PrintStream out = new PrintStream(client.getOutputStream());  
            //获取Socket的输入流，用来接收从客户端发送过来的数据  
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));  
            boolean flag =true;
            while(flag){  
                //接收从客户端发送过来的数据  
                String str =  buf.readLine();
                if(str == null || "".equals(str)){  
                	out.println("str为空指令错误，请输入正确的指令！！！");
                }else{  
                    if("bye".equals(str)){  
                        flag = false;  
                    }else{  
                        //将接收到的字符串前面加上echo，发送到对应的客户端  
                        FileManagement file = new FileManagement();
                		String op = new String();
                		String srcPath = new String();
                		String destDir = new String();
            			String path = new String();
            			if(str.trim().lastIndexOf(" ") == -1) {
            				op = str;
            			}
            			else {
            				String[] command = str.split(" ");
            				if(command.length == 2) {
            					op = command[0];
                				path = command[1];
            				}
            				else if(command.length == 3) {
            					op = command[0];
            					srcPath = command[1];
            					destDir = command[2];
            				}
            			}
            			if(path.isEmpty()) {
            				path = SystemPath;
            			}
            			ArrayList<String> temp = new ArrayList<String>();
            			path = path.replaceAll("\\\\", "\\\\\\\\");	
            			switch(op) {
                        //列出当前路径的文件目录
            			case "dir"	:{	
            				out.println(SystemPath + ">");
            				temp = file.getFiles(path); 
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //进入文件夹
            			case "cd"	:	{
            				SystemPath = file.showCd(SystemPath, path);
            				out.println(SystemPath + ">");
            				out.println(0);
            			}break;
                        //创建文件
            			case "cf"	:	{
            				String filename = path; 
            				temp = file.createFile(SystemPath + "\\", filename);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //创建文件夹
            			case "cfl"  : 	{
            				String fodlername = path; 
            				temp = file.createFolder(SystemPath + "\\", fodlername);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //删除文件
            			case "df"	:	{
            				String fname = path; 
            				temp = file.deleteFile(SystemPath + "\\", fname);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //删除文件夹
            			case "dfl"	:	{
            				String flname = path; 
            				temp = file.deleteFolder(SystemPath + "\\", flname);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //拷贝文件或文件夹
            			case "copy" :	{
            				temp = file.copyGeneralFile(srcPath, destDir);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //加密文件
            			case "enc"	:	{
            				String temppath = SystemPath + "\\" + path;
            				temp = file.encrypt(temppath, numOfEncAndDec);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //解密文件
            			case "dec"	:	{
            				String tempscr = SystemPath + "\\" + srcPath;
            				String tempdir = SystemPath + "\\" + destDir;
            				String teString = file.decrypt(tempscr, tempdir, numOfEncAndDec);
            				out.println(SystemPath + ">");
            				out.println(1);
            				out.println(teString);
            			}break;
                        //客户端上传到服务器
                        case "push" :   {
                        	@SuppressWarnings("resource")
							PrintWriter temppw = new PrintWriter(new FileWriter(destDir), true);
                        	String templine = null;
                        	while ((templine = buf.readLine()) != null) {
                        		if(templine.equals("#@#@Over@#@#")) {
                        			break;
                        		}
								temppw.println(templine);
							}
                        }break;
                        //从服务器将文件下载到客户端
                        case "pull" :   {
                        	@SuppressWarnings("resource")
							BufferedReader tempbf = new BufferedReader(new FileReader(srcPath));
        	        		OutputStream tempout = client.getOutputStream();
        	        		PrintWriter temppw = new PrintWriter(tempout, true);
        	        		String tempString = null;
        	        		while ((tempString = tempbf.readLine()) != null) {
        						temppw.println(tempString);
        					}
        	        		temppw.println("#@#@Over@#@#");
                        }break;
            			case "exit"	:	{
            				out.println("操作完成，服务器关闭，请尽快退出！！！");
            				out.println(0);
            				System.exit(0);
            			}break;
            			default:{
            				out.println("指令错误，请输入正确的指令！！！");
            				out.println(0);
            			}
            			}
                    }
                }  
            }
            out.close();  
            client.close();
            System.out.println("服务器关闭");
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  

}  