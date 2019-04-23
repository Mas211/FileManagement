package socket.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;  
import java.net.Socket;
import java.util.ArrayList;

import File.FileManagement;  

/** 
 * 该类为多线程类，用于服务端 
 */  
public class ServerThread implements Runnable {  
	public final static int bytesize = 2048;
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
                    flag = false;  
                }else{  
                    if("bye".equals(str)){  
                        flag = false;  
                    }else{  
                        //将接收到的字符串前面加上echo，发送到对应的客户端  
                        //out.println("收到:" + str);
                        FileManagement file = new FileManagement();
                		String op = new String();
                		String srcPath = new String();
                		String destDir = new String();
            			String path = new String();
            			//System.out.println(SystemPath + ">");
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
            				temp = file.encrypt(path, numOfEncAndDec);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //解密文件
            			case "dec"	:	{
            				String teString = file.decrypt(srcPath, destDir, numOfEncAndDec);
            				out.println(SystemPath + ">");
            				out.println(1);
            				out.println(teString);
            			}break;
                        //客户端上传到服务器
                        case "push" :   {
                        	InputStream ins = client.getInputStream();
                        	ObjectInputStream oisInputStream = new ObjectInputStream(ins);
                        	FileOutputStream fos = new FileOutputStream(destDir);
                        	System.out.println("开始读取文件....");
                        	//1.读取的数组长度
                        	int len = oisInputStream.readInt();
                        	//2.读取次数
                        	long times = oisInputStream.readLong();
                        	//3.读取最后一次字节长度
                        	int lastBytes = oisInputStream.readInt();
                        	byte[] bytes = new byte[len];
                        	//循环读取文件
                        	while (times > 1) {
                        		oisInputStream.readFully(bytes);
                        		fos.write(bytes);
                        		fos.flush();
                        		times--;
							}
                        	//处理最后一次字节数组
                        	bytes = new byte[lastBytes];
                        	oisInputStream.readFully(bytes);
                        	fos.write(bytes);
                        	fos.flush();
                        	System.out.println("文件传输完毕！");
                        	//oisInputStream.close();
                        	//fos.close();
                        }break;
                        //从服务器将文件下载到客户端
                        case "pull" :   {
                        	/*
                        	//获取Socket的输出流，用来向客户端发送数据  
                        	PrintStream out = new PrintStream(client.getOutputStream());  
                            //获取Socket的输入流，用来接收从客户端发送过来的数据  
                            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));  
                            */
                        	FileInputStream tempFileInputStream = new FileInputStream(srcPath);
                            OutputStream tempOutputStream = client.getOutputStream();
                            ObjectOutputStream tempObjectOutputStream = new ObjectOutputStream(tempOutputStream);
                            System.out.println("开始预处理要发送的文件...");
                            File tempFile = new File(srcPath);
                            long filesize = tempFile.length();
                            //先获取文件大小
                            byte[] bytes = new byte[bytesize];
                            //计算即将发送字节数组的字数
                            long times = filesize / bytesize + 1;
                            //计算最后一组字节数组的有效字节数
                            int lastBytes = (int)filesize%bytesize;
                            //1.发送字节数组长度
                            tempObjectOutputStream.writeInt(bytesize);
                            //2.发送次数
                            tempObjectOutputStream.writeLong(times);
                            tempObjectOutputStream.flush();
                            //3.最后一次字节个数
                            tempObjectOutputStream.writeInt(lastBytes);
                            tempObjectOutputStream.flush();
                            //读取字节数组长度的字节，返回读取字节数中的数据个数
                            int value = tempFileInputStream.read(bytes);
                            while (value != -1) {
                            	tempObjectOutputStream.write(bytes, 0, bytesize);
                            	tempObjectOutputStream.flush();
                            	value = tempFileInputStream.read(bytes);	
                            }
                            System.out.println("文件下载成功！");
                            //tempFileInputStream.close();
                            //tempOutputStream.close();
                            //tempObjectOutputStream.close();
                        }break;
            			case "exit"	:	{
            				//System.out.println("--exit--");
            				out.println("操作完成，服务器关闭，请尽快退出");
            				out.println(0);
            				System.exit(0);
            			}break;
            			default:{
            				out.println("指令错误，请输入正确的指令！！");
            				out.println(0);
            			}
            			}
                    }
                }  
            }  
            out.close();  
            client.close();  
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  

}  