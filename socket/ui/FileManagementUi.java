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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.awt.event.ActionEvent;
import java.awt.TextArea;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

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
	private JLabel label;
	
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
	        	//System.out.println(SystemPath + ">");
	        	if(str.trim().lastIndexOf(" ") == -1) {
	        		op = str;
	        	}
				else {
					String[] command = str.split(" ");
					if(command.length == 3) {
						op = command[0];
						srcpath = command[1];
						destDir = command[2];
						}
				}
	        	//发送数据到服务端 
	        	sBuffer.append("--------------------------\n");
	        	status.setText(sBuffer.toString());
	        	out.println(str);
	        	if (op.equals("push")) {
	        		@SuppressWarnings("resource")
					BufferedReader tempbf = new BufferedReader(new FileReader(srcpath));
	        		OutputStream tempout = clientSocket.getOutputStream();
	        		PrintWriter temppw = new PrintWriter(tempout, true);
	        		String tempString = null;
	        		while ((tempString = tempbf.readLine()) != null) {
						temppw.println(tempString);
					}
	        		temppw.println("#@#@Over@#@#");
	        		sBuffer.append("文件上传成功！\n");
                	status.setText(sBuffer.toString());
		        }
	        	else if(op.equals("pull")) {
	        		@SuppressWarnings("resource")
					PrintWriter temppw = new PrintWriter(new FileWriter(destDir), true);
                	String templine = null;
                	while ((templine = buf.readLine()) != null) {
                		if(templine.equals("#@#@Over@#@#")) {
                			break;
                		}
						temppw.println(templine);
					}
                	sBuffer.append("文件下载完毕！\n");
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
	        		}
		            catch(SocketTimeoutException e){  
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
		setForeground(Color.WHITE);
		setTitle("\u8FDC\u7A0B\u6587\u4EF6\u7BA1\u7406\u7CFB\u7EDF");
		clt = new Client();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 944, 555);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("\u83DC\u5355");
		menuBar.add(menu);
		
		JMenuItem helpMenu = new JMenuItem("\u5E2E\u52A9");
		helpMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "帮助文档\n"
						+ "dir 	显示当前路径所有文件\n"
						+ "cd 	进入文件夹，例：cd E: or cd E:\test2\n"
						+ "copy	复制文件，第一个参数为当前路径下的文件名，第二个参数为"
						+ "目的地址中的文件夹名，例：copy 1.txt E:\test2\123\n"
						+ "cf 	创建文件，输入创建的文件名，例：cf 4.txt\n"
						+ "cfl	创建文件，输入创建的文件夹名，例：cfl 123\n"
						+ "df	删除文件，输入需删除的文件，例：df 1.txt\n"
						+ "dfl 	删除文件夹，输入需删除的文件夹，例：df 123\n"
						+ "enc	加密文件，输入需加密的文件名，例：enc 1.txt\n"
						+ "dec	解密文件，第一个参数输入需解密的文件名，第二个参数输入解密后的文件，例：dec 1.txt 2.txt\n"
						+ "push	上传文件，第一个参数客户端需上传的文件的绝对地址，第二个参数为将该\n"
						+ "文件放置在服务器的某个位置，也是绝对地址，例: push E:\\test2\\1.txt E:\\test2\\2.txt\n"
						+ "pull 下载文件，第一个参数为需下载服务器的文件的绝对地址，"
						+ "第二个参数为客户端将文件下载到的位置，也是绝对地址，例：pull E:\\test2\\1.txt E:\\test2\\2.txt\n", 
						"帮助文档", JOptionPane.NO_OPTION);
			}
		});
		menu.add(helpMenu);
		
		JMenuItem aboutMenu = new JMenuItem("\u5173\u4E8E");
		menu.add(aboutMenu);
		aboutMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "版本 v1.3  - 2019年4月\n"
						+ "作者：mas", "关于", JOptionPane.NO_OPTION);
			}
		});
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
		hostText.setText("127.0.0.1");
		
		protText = new JTextField();
		protText.setColumns(10);
		protText.setBounds(335, 39, 66, 21);
		contentPane.add(protText);
		protText.setText("10086");
		
		Portlabel = new JLabel("\u7AEF\u53E3");
		Portlabel.setFont(new Font("宋体", Font.PLAIN, 16));
		Portlabel.setBounds(286, 41, 39, 15);
		contentPane.add(Portlabel);
		
		status = new TextArea();
		status.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		status.setBounds(10, 265, 538, 241);
		contentPane.add(status);
		
		JLabel oplabel = new JLabel("\u64CD\u4F5C\u7801");
		oplabel.setFont(new Font("宋体", Font.PLAIN, 16));
		oplabel.setBounds(22, 108, 96, 23);
		contentPane.add(oplabel);
		
		opText = new JTextField();
		opText.setColumns(10);
		opText.setBounds(216, 110, 176, 21);
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
		srcpathText.setBounds(216, 143, 176, 21);
		contentPane.add(srcpathText);
		
		despathText = new JTextField();
		despathText.setColumns(10);
		despathText.setBounds(216, 176, 176, 21);
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
		
		label = new JLabel("\u8FD0\u884C\u7ED3\u679C");
		label.setFont(new Font("新宋体", Font.PLAIN, 16));
		label.setBounds(22, 234, 108, 25);
		contentPane.add(label);
		
	}
}
