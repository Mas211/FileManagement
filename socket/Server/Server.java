package socket.Server;

import java.net.ServerSocket;  
import java.net.Socket;  

public class Server {  
    public static void main(String[] args) throws Exception{  
        //�������10086�˿ڼ����ͻ��������TCP����  
        ServerSocket server = new ServerSocket(10086);  
        Socket client = null;  
        System.out.println("�����������������ڼ���10086�˿�...");
        //�ȴ��ͻ��˵����ӣ����û�л�ȡ����  
        client = server.accept();  
        System.out.println("��ͻ������ӳɹ���");  
        //Ϊÿ���ͻ������ӿ���һ���߳�  
        new Thread(new ServerThread(client)).start();
        server.close();
    }  
} 