package socket.Client;

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
import java.net.SocketTimeoutException;

public class Client {  

	public final static int bytesize = 2048;
    public static void main(String[] args) throws Exception {  
        //�ͻ��������뱾����20006�˿ڽ���TCP����   
        Socket client = new Socket("127.0.0.1", 10086);  
        client.setSoTimeout(10000);  
        //��ȡ��������   
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));  
        //��ȡSocket��������������������ݵ������    
        PrintStream out = new PrintStream(client.getOutputStream());  
        //��ȡSocket�����������������մӷ���˷��͹���������    
        BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));  
        boolean flag = true;  
        while(flag){  
            System.out.print("������Ϣ��");
            String str = input.readLine();
            String op = new String();
            String srcpath = new String();
    		String destDir = new String();
            //System.out.println(SystemPath + ">");
            if(str.trim().lastIndexOf(" ") == -1) {
				op = str;
			}
			else {
				String[] command = str.split(" ");
				if(command.length == 2) {
					op = command[0];
				}
				else if(command.length == 3) {
					op = command[0];
					srcpath = command[1];
					destDir = command[2];
				}
			}
            //�������ݵ������ 
            out.println(str);
            if("bye".equals(str)){
                flag = false;  
            }
            else if (op.equals("push")) {
            	/*push I:\test2\3.txt I:\test2\1.txt*/
                //���ص������� 
                @SuppressWarnings("resource")
				FileInputStream tempFileInputStream = new FileInputStream(srcpath);
                OutputStream tempOutputStream = client.getOutputStream();
                ObjectOutputStream tempObjectOutputStream = new ObjectOutputStream(tempOutputStream);
                System.out.println("��ʼԤ����Ҫ���͵��ļ�...");
                File tempFile = new File(srcpath);
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
                System.out.println("�ļ��ϴ��ɹ���");
                //tempFileInputStream.close();
                //tempOutputStream.close();
                //tempObjectOutputStream.close();
            }
            else if(op.equals("pull")) {
            	InputStream ins = client.getInputStream();
            	ObjectInputStream oisInputStream = new ObjectInputStream(ins);
            	@SuppressWarnings("resource")
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
            }
            else{  
                try{
                    //�ӷ������˽��������и�ʱ�����ƣ�ϵͳ���裬Ҳ�����Լ����ã������������ʱ�䣬����׳����쳣  
                    //String echo = buf.readLine();  
                    //System.out.println(echo);
                	/*
                	String num = buf.readLine();
                	System.out.println(num);
                	for (int i = 0; i < num.indexOf(num); i++) {
						String eco = buf.readLine();
						System.out.println(eco);
					}*/
                	String echo = buf.readLine();  //�����յ����ǵ�ǰ·��
                	System.out.println(echo);      
                	String num = buf.readLine();   //�������ĸ���
                	//System.out.println(num);
                	for (int i = 0; i < Integer.parseInt(num); i++) {
						String eco = buf.readLine();
						System.out.println(eco);
					}
                	/*
                	while(echo != null){
                		echo = buf.readLine();
                        System.out.println(echo);
                	}*/
                }catch(SocketTimeoutException e){  
                    System.out.println("Time out, No response");  
                } 
            }  
        }  
        input.close();  
        if(client != null){  
            //������캯�������������ӣ���ر��׽��֣����û�н��������ӣ���Ȼ���ùر�  
            client.close(); //ֻ�ر�socket������������������Ҳ�ᱻ�ر�  
        }  
    }
} 