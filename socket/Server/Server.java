package socket.Server;

import java.net.ServerSocket;  
import java.net.Socket;  

public class Server {  
    public static void main(String[] args) throws Exception{  
        //服务端在10086端口监听客户端请求的TCP连接  
        ServerSocket server = new ServerSocket(10086);  
        Socket client = null;  
        System.out.println("服务器已启动，正在监听10086端口...");
        //等待客户端的连接，如果没有获取连接  
        client = server.accept();  
        System.out.println("与客户端连接成功！");  
        //为每个客户端连接开启一个线程  
        new Thread(new ServerThread(client)).start();
        server.close();
    }  
} 