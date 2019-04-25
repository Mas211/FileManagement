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
	private JButton cdButton;
	private JButton copyButton;
	private JButton cfButton;
	private JButton deleteButton;
	private JButton deleteDirButton;
	private JButton encButton;
	private JButton decButton;
	private JButton pushButton;
	private JButton pullButton;
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
	        	//发送数据到服务端 
	        	out.println(str);
	        	if (op.equals("push")) {
		        	/*push I:\test2\3.txt I:\test2\1.txt*/
		        	//本地的输入流 
		        	FileInputStream tempFileInputStream = new FileInputStream(srcpath);
		        	OutputStream tempOutputStream = clientSocket.getOutputStream();
		        	ObjectOutputStream tempObjectOutputStream = new ObjectOutputStream(tempOutputStream);
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
		        	/*
		        	while (value != -1) {
		        		tempObjectOutputStream.write(bytes, 0, bytesize);
		        		tempObjectOutputStream.flush();
		        		value = tempFileInputStream.read(bytes);	
		        	}*/
		        	while (times > 0) {
                		tempObjectOutputStream.write(bytes, 0, bytesize);
                		tempObjectOutputStream.flush();
                		times--;
					}
		        	sBuffer.append("文件上传成功！" + "\n");
		        	status.setText(sBuffer.toString());
		        	tempFileInputStream.close();
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
		            fos.close();
		            sBuffer.append("文件下载完毕！" + "\n");
		            status.setText(sBuffer.toString());
		        }
	        	else{  
	        		try{
	        			//从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常 
	        			String echo = buf.readLine();  //首先收到的是当前路径   
		                sBuffer.append(echo + "\n");
		                status.setText(sBuffer.toString());
		                String num = buf.readLine();   //后面语句的个数
		                for (int i = 0; i < Integer.parseInt(num); i++) {
							String eco = buf.readLine();
							sBuffer.append(eco + "\n");
				            status.setText(sBuffer.toString());
				        }
		                }catch(SocketTimeoutException e){  
		                	sBuffer.append("Time out, No response" + "\n");
		                	status.setText(sBuffer.toString());
		                	status.getText();
		                } 
	        		}  
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
		connectButton.setFont(new Font("新宋体", Font.PLAIN, 16));
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
		connectButton.setBounds(440, 29, 108, 40);
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
		status.setFont(new Font("Dialog", Font.PLAIN, 14));
		status.setBounds(10, 265, 538, 241);
		contentPane.add(status);
		
		JLabel oplabel = new JLabel("\u64CD\u4F5C\u7801");
		oplabel.setFont(new Font("宋体", Font.PLAIN, 16));
		oplabel.setBounds(22, 108, 96, 23);
		contentPane.add(oplabel);
		
		opText = new JTextField();
		opText.setColumns(10);
		opText.setBounds(322, 110, 176, 21);
		contentPane.add(opText);
		
		JLabel srcpathlabel = new JLabel("\u8BF7\u8F93\u5165\u539F\u8DEF\u5F84");
		srcpathlabel.setFont(new Font("宋体", Font.PLAIN, 16));
		srcpathlabel.setBounds(22, 141, 217, 23);
		contentPane.add(srcpathlabel);
		
		JLabel despathlabel = new JLabel("\u8BF7\u8F93\u5165\u76EE\u7684\u8DEF\u5F84");
		despathlabel.setFont(new Font("宋体", Font.PLAIN, 16));
		despathlabel.setBounds(22, 174, 267, 23);
		contentPane.add(despathlabel);
		
		srcpathText = new JTextField();
		srcpathText.setColumns(10);
		srcpathText.setBounds(322, 143, 176, 21);
		contentPane.add(srcpathText);
		
		despathText = new JTextField();
		despathText.setColumns(10);
		despathText.setBounds(322, 176, 176, 21);
		contentPane.add(despathText);
		
		JButton sendButton = new JButton("\u53D1\u9001\u547D\u4EE4");
		sendButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(srcpathText.getText().trim().equals("")){
					clt.str = opText.getText();
					opText.setText("");
				}
				else if(despathText.getText().trim().equals("") && (!srcpathText.getText().trim().equals(""))){
					clt.str = (opText.getText() + " " + srcpathText.getText());
					opText.setText("");
					srcpathText.setText("");
				}
				else {
					clt.str = (opText.getText() + " " + srcpathText.getText() + " " + despathText.getText());
					opText.setText("");
					srcpathText.setText("");
					despathText.setText("");
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
		sendButton.setBounds(409, 209, 139, 42);
		contentPane.add(sendButton);
		
		JButton dirButton = new JButton("\u663E\u793A\u5F53\u524D\u8DEF\u5F84\u7684\u6240\u6709\u6587\u4EF6");
		dirButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		dirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("dir");
				srcpathlabel.setText("");
				despathlabel.setText("");
				srcpathText.setEnabled(false);
				despathText.setEditable(false);
			}
		});
		dirButton.setBounds(650, 24, 224, 32);
		contentPane.add(dirButton);
		
		cdButton = new JButton("\u8FDB\u5165\u6587\u4EF6\u5939");
		cdButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("cd");
				srcpathlabel.setText("要进入的路径：");
				despathlabel.setText("");
				srcpathText.setEnabled(true);
				despathText.setEditable(false);
				srcpathText.getText();
			}
		});
		cdButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		cdButton.setBounds(650, 66, 224, 32);
		contentPane.add(cdButton);
		
		copyButton = new JButton("\u590D\u5236\u6587\u4EF6");
		copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("copy");
				srcpathlabel.setText("需拷贝文件的路径：");
				despathlabel.setText("拷贝到哪：");
				srcpathText.setEnabled(true);
				despathText.setEditable(true);
				srcpathText.getText();
				despathText.getText();
			}
		});
		copyButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		copyButton.setBounds(650, 108, 224, 32);
		contentPane.add(copyButton);
		
		cfButton = new JButton("\u521B\u5EFA\u6587\u4EF6");
		cfButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("cf");
				srcpathlabel.setText("文件名：");
				despathlabel.setText("");
				srcpathText.setEnabled(true);
				despathText.setEditable(false);
				srcpathText.getText();
			}
		});
		cfButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		cfButton.setBounds(650, 150, 224, 32);
		contentPane.add(cfButton);
		
		deleteButton = new JButton("\u5220\u9664\u6587\u4EF6");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("df");
				srcpathlabel.setText("需删除的文件名：");
				despathlabel.setText("");
				srcpathText.setEnabled(true);
				despathText.setEditable(false);
				srcpathText.getText();
			}
		});
		deleteButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		deleteButton.setBounds(650, 234, 224, 32);
		contentPane.add(deleteButton);
		
		deleteDirButton = new JButton("\u5220\u9664\u6587\u4EF6\u5939");
		deleteDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("dfl");
				srcpathlabel.setText("需删除的文件名夹：");
				despathlabel.setText("");
				srcpathText.setEnabled(true);
				despathText.setEditable(false);
				srcpathText.getText();
			}
		});
		deleteDirButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		deleteDirButton.setBounds(650, 277, 224, 32);
		contentPane.add(deleteDirButton);
		
		encButton = new JButton("\u52A0\u5BC6\u6587\u4EF6");
		encButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("enc");
				srcpathlabel.setText("需加密的文件名：");
				despathlabel.setText("");
				srcpathText.setEnabled(true);
				despathText.setEditable(false);
				srcpathText.getText();
			}
		});
		encButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		encButton.setBounds(650, 319, 224, 32);
		contentPane.add(encButton);
		
		decButton = new JButton("\u89E3\u5BC6");
		decButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("dec");
				srcpathlabel.setText("需解密的文件名：");
				despathlabel.setText("解密后放在的文件：");
				srcpathText.setEnabled(true);
				despathText.setEditable(true);
				srcpathText.getText();
				despathText.getText();
			}
		});
		decButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		decButton.setBounds(650, 361, 224, 32);
		contentPane.add(decButton);
		
		pushButton = new JButton("\u4E0A\u4F20\u6587\u4EF6");
		pushButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("push");
				srcpathlabel.setText("需上传的文件名：");
				despathlabel.setText("存在服务器的路径：");
				srcpathText.setEnabled(true);
				despathText.setEditable(true);
				srcpathText.getText();
				despathText.getText();
			}
		});
		pushButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		pushButton.setBounds(650, 403, 224, 32);
		contentPane.add(pushButton);
		
		pullButton = new JButton("\u4E0B\u8F7D\u6587\u4EF6");
		pullButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("pull");
				srcpathlabel.setText("需下载的文件：");
				despathlabel.setText("存在客户端的路径：");
				srcpathText.setEnabled(true);
				despathText.setEditable(true);
				srcpathText.getText();
				despathText.getText();
			}
		});
		pullButton.setFont(new Font("新宋体", Font.PLAIN, 16));
		pullButton.setBounds(650, 445, 224, 32);
		contentPane.add(pullButton);
		
		JButton cflbutton = new JButton("\u521B\u5EFA\u6587\u4EF6\u5939");
		cflbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opText.setText("cfl");
				srcpathlabel.setText("文件夹名：");
				despathlabel.setText("");
				srcpathText.setEnabled(true);
				despathText.setEditable(false);
				srcpathText.getText();
			}
		});
		cflbutton.setFont(new Font("新宋体", Font.PLAIN, 16));
		cflbutton.setBounds(650, 192, 224, 32);
		contentPane.add(cflbutton);
	}
}
