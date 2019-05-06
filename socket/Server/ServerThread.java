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
 * ����Ϊ���߳��࣬���ڷ���� 
 */  
public class ServerThread implements Runnable {  
	public final static int bytesize = 2048;
	//��Կ
	private static final int numOfEncAndDec = 20164277;
    private Socket client = null;  
    public ServerThread(Socket client){  
        this.client = client;  
    }  

    @Override  
    public void run() {  
        try{  
        	File directory = new File("");//�趨Ϊ��ǰ�ļ���
    		String SystemPath = directory.getAbsolutePath();
            //��ȡSocket���������������ͻ��˷�������  
            PrintStream out = new PrintStream(client.getOutputStream());  
            //��ȡSocket�����������������մӿͻ��˷��͹���������  
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));  
            boolean flag =true;
            while(flag){  
                //���մӿͻ��˷��͹���������  
                String str =  buf.readLine();
                if(str == null || "".equals(str)){  
                	out.println("strΪ��ָ�������������ȷ��ָ�����");
                }else{  
                    if("bye".equals(str)){  
                        flag = false;  
                    }else{  
                        //�����յ����ַ���ǰ�����echo�����͵���Ӧ�Ŀͻ���  
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
                        //�г���ǰ·�����ļ�Ŀ¼
            			case "dir"	:{	
            				out.println(SystemPath + ">");
            				temp = file.getFiles(path); 
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //�����ļ���
            			case "cd"	:	{
            				SystemPath = file.showCd(SystemPath, path);
            				out.println(SystemPath + ">");
            				out.println(0);
            			}break;
                        //�����ļ�
            			case "cf"	:	{
            				String filename = path; 
            				temp = file.createFile(SystemPath + "\\", filename);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //�����ļ���
            			case "cfl"  : 	{
            				String fodlername = path; 
            				temp = file.createFolder(SystemPath + "\\", fodlername);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //ɾ���ļ�
            			case "df"	:	{
            				String fname = path; 
            				temp = file.deleteFile(SystemPath + "\\", fname);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //ɾ���ļ���
            			case "dfl"	:	{
            				String flname = path; 
            				temp = file.deleteFolder(SystemPath + "\\", flname);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //�����ļ����ļ���
            			case "copy" :	{
            				temp = file.copyGeneralFile(srcPath, destDir);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //�����ļ�
            			case "enc"	:	{
            				String temppath = SystemPath + "\\" + path;
            				temp = file.encrypt(temppath, numOfEncAndDec);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //�����ļ�
            			case "dec"	:	{
            				String tempscr = SystemPath + "\\" + srcPath;
            				String tempdir = SystemPath + "\\" + destDir;
            				String teString = file.decrypt(tempscr, tempdir, numOfEncAndDec);
            				out.println(SystemPath + ">");
            				out.println(1);
            				out.println(teString);
            			}break;
                        //�ͻ����ϴ���������
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
                        //�ӷ��������ļ����ص��ͻ���
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
            				out.println("������ɣ��������رգ��뾡���˳�������");
            				out.println(0);
            				System.exit(0);
            			}break;
            			default:{
            				out.println("ָ�������������ȷ��ָ�����");
            				out.println(0);
            			}
            			}
                    }
                }  
            }
            out.close();  
            client.close();
            System.out.println("�������ر�");
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  

}  