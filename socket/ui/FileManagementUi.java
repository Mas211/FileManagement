package socket.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.awt.event.ActionEvent;
import java.awt.TextArea;
import java.awt.Font;

public class FileManagementUi extends JFrame {
	/**
	 * Launch the application.
	 */
	Client clt = new Client();
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField hostText;
	private JTextField protText;
	private JLabel Portlabel;
	private JTextField opText;
	private JTextField srcpathText;
	private JTextField despathText;
	private TextArea status;
	Socket clientSocket = new Socket();
	StringBuffer sBuffer  =  new StringBuffer();
	
	public class Client {
		public String  str = "xxx"; 
		public final static int bytesize = 2048;
		
		public void sendMessage(Socket c) throws IOException {
	        //获取Socket的输出流，用来发送数据到服务端    
	        PrintStream out = new PrintStream(clientSocket.getOutputStream());  
	        //获取Socket的输入流，用来接收从服务端发送过来的数据    
	        BufferedReader buf =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  
	        //System.out.println("1");
	        boolean flag = true;
	        while(flag){  
	        	if (!str.equals("xxx")) {
		            String op = new String();
		            String srcpath = new String();
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
							srcpath = command[1];
							destDir = command[2];
						}
					}
		            //System.out.println(op);
		            //发送数据到服务端 
		            out.println(str);
		            if("bye".equals(str)){
		                flag = false;  
		            }
		            else if (op.equals("push")) {
		            	/*push I:\test2\3.txt I:\test2\1.txt*/
		                //本地的输入流 
		                //@SuppressWarnings("resource")
						FileInputStream tempFileInputStream = new FileInputStream(srcpath);
		                OutputStream tempOutputStream = clientSocket.getOutputStream();
		                ObjectOutputStream tempObjectOutputStream = new ObjectOutputStream(tempOutputStream);
		                //System.out.println("开始预处理要发送的文件...");
		                sBuffer.append("开始预处理要发送的文件..." + "\n");
		                status.setText(sBuffer.toString());
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
		                //System.out.println("文件上传成功！");
		                sBuffer.append("文件上传成功！" + "\n");
		                status.setText(sBuffer.toString());
		            }
		            else if(op.equals("pull")) {
		            	InputStream ins = clientSocket.getInputStream();
		            	ObjectInputStream oisInputStream = new ObjectInputStream(ins);
		            	FileOutputStream fos = new FileOutputStream(destDir);
		            	//System.out.println("开始读取文件....");
		            	sBuffer.append("开始读取文件...." + "\n");
		                status.setText(sBuffer.toString());
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
		            	//System.out.println("文件下载完毕！");
		            	sBuffer.append("文件下载完毕！" + "\n");
		                status.setText(sBuffer.toString());
		            }
		            else{  
		                try{
		                    //从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常 
		                	String echo = buf.readLine();  //首先收到的是当前路径
		                	//System.out.println(echo);    
		                	sBuffer.append(echo + "\n");
			                status.setText(sBuffer.toString());
		                	String num = buf.readLine();   //后面语句的个数
		                	//System.out.println(num);
		                	for (int i = 0; i < Integer.parseInt(num); i++) {
								String eco = buf.readLine();
								//System.out.println(eco);
								sBuffer.append(eco + "\n");
				                status.setText(sBuffer.toString());
							}
		                	flag = false;
		                }catch(SocketTimeoutException e){  
		                    //System.out.println("Time out, No response");  
		                    sBuffer.append("Time out, No response" + "\n");
			                status.setText(sBuffer.toString());
		                } 
		            }  
		        } 
	        	/*
		        if(clientSocket != null){  
		            //如果构造函数建立起了连接，则关闭套接字，如果没有建立起连接，自然不用关闭  
		            clientSocket.close(); //只关闭socket，其关联的输入输出流也会被关闭  
		            System.out.println("关闭");
		        }  */
			}
		}
		
	    public  void run(String host, int port) throws Exception {  
	        //客户端请求与本机在20006端口建立TCP连接   
	        clientSocket = new Socket(host, port);  
	        clientSocket.setSoTimeout(10000);
	    }
	} 
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileManagementUi frame = new FileManagementUi();
					frame.setVisible(true);
//					int portInt = Integer.parseInt(frame.protText.getText());
//					String hostString = frame.hostText.getText();
//					frame.clt.run(hostString, portInt);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FileManagementUi() {
		clt = new Client();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 944, 555);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel HostLabel = new JLabel("IP\u5730\u5740");
		HostLabel.setFont(new Font("宋体", Font.PLAIN, 16));
		HostLabel.setBounds(21, 41, 54, 15);
		contentPane.add(HostLabel);
		
		JButton connectButton = new JButton("\u94FE\u63A5");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String hostString = hostText.getText();
				int portInt = Integer.parseInt(protText.getText());
				try {
					clt.run(hostString, portInt);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		connectButton.setBounds(445, 38, 93, 23);
		contentPane.add(connectButton);
		
		hostText = new JTextField();
		hostText.setBounds(85, 39, 176, 21);
		contentPane.add(hostText);
		hostText.setColumns(10);
		
		protText = new JTextField();
		protText.setColumns(10);
		protText.setBounds(335, 39, 66, 21);
		contentPane.add(protText);
		
		Portlabel = new JLabel("\u7AEF\u53E3");
		Portlabel.setFont(new Font("宋体", Font.PLAIN, 16));
		Portlabel.setBounds(286, 41, 39, 15);
		contentPane.add(Portlabel);
		
		status = new TextArea();
		status.setBounds(10, 265, 533, 243);
		contentPane.add(status);
		
		JLabel oplabel = new JLabel("\u8BF7\u8F93\u5165\u64CD\u4F5C\u7801");
		oplabel.setFont(new Font("宋体", Font.PLAIN, 16));
		oplabel.setBounds(22, 108, 96, 23);
		contentPane.add(oplabel);
		
		opText = new JTextField();
		opText.setColumns(10);
		opText.setBounds(167, 110, 176, 21);
		contentPane.add(opText);
		
		JLabel srcpathlabel = new JLabel("\u8BF7\u8F93\u5165\u539F\u8DEF\u5F84");
		srcpathlabel.setFont(new Font("宋体", Font.PLAIN, 16));
		srcpathlabel.setBounds(22, 141, 96, 23);
		contentPane.add(srcpathlabel);
		
		JLabel despathlabel = new JLabel("\u8BF7\u8F93\u5165\u76EE\u7684\u8DEF\u5F84");
		despathlabel.setFont(new Font("宋体", Font.PLAIN, 16));
		despathlabel.setBounds(22, 174, 124, 23);
		contentPane.add(despathlabel);
		
		srcpathText = new JTextField();
		srcpathText.setColumns(10);
		srcpathText.setBounds(167, 143, 176, 21);
		contentPane.add(srcpathText);
		
		despathText = new JTextField();
		despathText.setColumns(10);
		despathText.setBounds(167, 176, 176, 21);
		contentPane.add(despathText);
		
		JButton sendButton = new JButton("\u53D1\u9001\u547D\u4EE4");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(srcpathText.getText().trim().equals("")){
					clt.str = opText.getText();
				}
				else if(despathlabel.getText().trim().equals("")){
					clt.str = (opText.getText() + " " + srcpathlabel.getText());
				}
				else {
					clt.str = (opText.getText() + " " + srcpathText.getText() + " " + despathText.getText());
				}
				System.out.println(clt.str);
				try {
					clt.sendMessage(clientSocket);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		sendButton.setBounds(51, 217, 97, 23);
		contentPane.add(sendButton);
	}
}
