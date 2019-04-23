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
 * ����Ϊ���߳��࣬���ڷ���� 
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
                    flag = false;  
                }else{  
                    if("bye".equals(str)){  
                        flag = false;  
                    }else{  
                        //�����յ����ַ���ǰ�����echo�����͵���Ӧ�Ŀͻ���  
                        //out.println("�յ�:" + str);
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
            				temp = file.encrypt(path, numOfEncAndDec);
            				out.println(SystemPath + ">");
            				out.println(temp.size());
            				for (String string : temp) {
								out.println(string);
							}
            			}break;
                        //�����ļ�
            			case "dec"	:	{
            				String teString = file.decrypt(srcPath, destDir, numOfEncAndDec);
            				out.println(SystemPath + ">");
            				out.println(1);
            				out.println(teString);
            			}break;
                        //�ͻ����ϴ���������
                        case "push" :   {
                        	InputStream ins = client.getInputStream();
                        	ObjectInputStream oisInputStream = new ObjectInputStream(ins);
                        	FileOutputStream fos = new FileOutputStream(destDir);
                        	System.out.println("��ʼ��ȡ�ļ�....");
                        	//1.��ȡ�����鳤��
                        	int len = oisInputStream.readInt();
                        	//2.��ȡ����
                        	long times = oisInputStream.readLong();
                        	//3.��ȡ���һ���ֽڳ���
                        	int lastBytes = oisInputStream.readInt();
                        	byte[] bytes = new byte[len];
                        	//ѭ����ȡ�ļ�
                        	while (times > 1) {
                        		oisInputStream.readFully(bytes);
                        		fos.write(bytes);
                        		fos.flush();
                        		times--;
							}
                        	//�������һ���ֽ�����
                        	bytes = new byte[lastBytes];
                        	oisInputStream.readFully(bytes);
                        	fos.write(bytes);
                        	fos.flush();
                        	System.out.println("�ļ�������ϣ�");
                        	//oisInputStream.close();
                        	//fos.close();
                        }break;
                        //�ӷ��������ļ����ص��ͻ���
                        case "pull" :   {
                        	/*
                        	//��ȡSocket���������������ͻ��˷�������  
                        	PrintStream out = new PrintStream(client.getOutputStream());  
                            //��ȡSocket�����������������մӿͻ��˷��͹���������  
                            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));  
                            */
                        	FileInputStream tempFileInputStream = new FileInputStream(srcPath);
                            OutputStream tempOutputStream = client.getOutputStream();
                            ObjectOutputStream tempObjectOutputStream = new ObjectOutputStream(tempOutputStream);
                            System.out.println("��ʼԤ����Ҫ���͵��ļ�...");
                            File tempFile = new File(srcPath);
                            long filesize = tempFile.length();
                            //�Ȼ�ȡ�ļ���С
                            byte[] bytes = new byte[bytesize];
                            //���㼴�������ֽ����������
                            long times = filesize / bytesize + 1;
                            //�������һ���ֽ��������Ч�ֽ���
                            int lastBytes = (int)filesize%bytesize;
                            //1.�����ֽ����鳤��
                            tempObjectOutputStream.writeInt(bytesize);
                            //2.���ʹ���
                            tempObjectOutputStream.writeLong(times);
                            tempObjectOutputStream.flush();
                            //3.���һ���ֽڸ���
                            tempObjectOutputStream.writeInt(lastBytes);
                            tempObjectOutputStream.flush();
                            //��ȡ�ֽ����鳤�ȵ��ֽڣ����ض�ȡ�ֽ����е����ݸ���
                            int value = tempFileInputStream.read(bytes);
                            while (value != -1) {
                            	tempObjectOutputStream.write(bytes, 0, bytesize);
                            	tempObjectOutputStream.flush();
                            	value = tempFileInputStream.read(bytes);	
                            }
                            System.out.println("�ļ����سɹ���");
                            //tempFileInputStream.close();
                            //tempOutputStream.close();
                            //tempObjectOutputStream.close();
                        }break;
            			case "exit"	:	{
            				//System.out.println("--exit--");
            				out.println("������ɣ��������رգ��뾡���˳�");
            				out.println(0);
            				System.exit(0);
            			}break;
            			default:{
            				out.println("ָ�������������ȷ��ָ���");
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