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
        //客户端请求与本机在20006端口建立TCP连接   
        Socket client = new Socket("127.0.0.1", 10086);  
        client.setSoTimeout(10000);  
        //获取键盘输入   
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));  
        //获取Socket的输出流，用来发送数据到服务端    
        PrintStream out = new PrintStream(client.getOutputStream());  
        //获取Socket的输入流，用来接收从服务端发送过来的数据    
        BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));  
        boolean flag = true;  
        while(flag){  
            System.out.print("输入信息：");
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
            //发送数据到服务端 
            out.println(str);
            if("bye".equals(str)){
                flag = false;  
            }
            else if (op.equals("push")) {
            	/*push I:\test2\3.txt I:\test2\1.txt*/
                //本地的输入流 
                @SuppressWarnings("resource")
				FileInputStream tempFileInputStream = new FileInputStream(srcpath);
                OutputStream tempOutputStream = client.getOutputStream();
                ObjectOutputStream tempObjectOutputStream = new ObjectOutputStream(tempOutputStream);
                System.out.println("开始预处理要发送的文件...");
                File tempFile = new File(srcpath);
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
                System.out.println("文件上传成功！");
                //tempFileInputStream.close();
                //tempOutputStream.close();
                //tempObjectOutputStream.close();
            }
            else if(op.equals("pull")) {
            	InputStream ins = client.getInputStream();
            	ObjectInputStream oisInputStream = new ObjectInputStream(ins);
            	@SuppressWarnings("resource")
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
            	System.out.println("文件下载完毕！");
            	//oisInputStream.close();
            	//fos.close();
            }
            else{  
                try{
                    //从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常  
                    //String echo = buf.readLine();  
                    //System.out.println(echo);
                	/*
                	String num = buf.readLine();
                	System.out.println(num);
                	for (int i = 0; i < num.indexOf(num); i++) {
						String eco = buf.readLine();
						System.out.println(eco);
					}*/
                	String echo = buf.readLine();  //首先收到的是当前路径
                	System.out.println(echo);      
                	String num = buf.readLine();   //后面语句的个数
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
            //如果构造函数建立起了连接，则关闭套接字，如果没有建立起连接，自然不用关闭  
            client.close(); //只关闭socket，其关联的输入输出流也会被关闭  
        }  
    }
} 