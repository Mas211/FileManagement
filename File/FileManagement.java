package File;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;


public class FileManagement {
	//�����ļ�
	public ArrayList<String> createFile(String path,String fileName) {
		ArrayList<String> temp = new ArrayList<String>();
		String str = path + fileName;
		File file = new File(str);
		if (file.exists()) {
            //System.out.println("���ļ��Ѵ��ڡ�");
			temp.add("���ļ��Ѵ��ڡ�");
        } else {
            //System.out.println("���ļ������ڣ��������ļ���");
        	temp.add("���ļ������ڣ��������ļ���");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return temp;
	}
	
	//�����ļ���
	public ArrayList<String> createFolder(String path, String folderName){
		ArrayList<String> temp = new ArrayList<String>();
		String str = path + folderName;
		File dir = new File(str);
		if (dir.exists()) {
            if (dir.isDirectory()) {
                //System.out.println("���ļ����Ѵ��ڡ�");
            	temp.add("���ļ����Ѵ��ڡ�");
            } else {
                //System.out.println("����ͬ���ֵ��ļ��д��ڣ����ܴ�����");
            	temp.add("����ͬ���ֵ��ļ��д��ڣ����ܴ�����");
            }
        } else {
            //System.out.println("���ļ��в����ڣ��������ļ��С�");
            temp.add("���ļ��в����ڣ��������ļ��С�");
            dir.mkdirs();
        }
		return temp;
	}
	
	//ɾ���ļ�
	public ArrayList<String> deleteFile(String path, String fileName) {
		ArrayList<String>  temp = new ArrayList<String>();
		String str = path + fileName;
		File file = new File(str);
		if (file.exists()) {
            //�ļ�����
            //System.out.println(file.getAbsolutePath()+"���ڡ�");
            String tString = file.getAbsolutePath() + "����";
            temp.add(tString);
            file.delete();
            //System.out.println("���ļ���ɾ����");
            temp.add("���ļ���ɾ����");
        } else {
            //�ļ�ѹ��������
            //System.out.println("�ļ������ڡ�");
        	temp.add("�ļ������ڡ�");
        }
		return temp;
	}
	
	//ɾ���ļ���
	public ArrayList<String> deleteFolder(String path, String folderName) {
		boolean flag = true;
		ArrayList<String> temp = new ArrayList<String>();
		String str = path + folderName;
		File dirfile = new File(str);
		if(!dirfile.isDirectory()) {
			flag = true;
			//System.out.println("�ļ��в�����!");
			temp.add("�ļ��в����ڣ�");
		}
		File[] files = dirfile.listFiles();
		for (File file : files) {
			if(file.isFile()) {
				ArrayList<String> tempFile = deleteFile(str + "\\", file.getName());
				for(String string : tempFile) {
					temp.add(string);
				}
				//System.out.println(str);
				//System.out.println(file.getName());
			}else if (file.isDirectory()) {
				ArrayList<String> tempDir = deleteFolder(str + "\\", file.getName());
				for(String string : tempDir) {
					temp.add(string);
				}
			}
			if(!flag) {
				break;
			}
		}
		flag = dirfile.delete();
		if(flag) {
			//System.out.println("ɾ���ļ��гɹ���");
			temp.add("ɾ���ļ��гɹ���");
		}
		else {
			//System.out.println("ɾ���ļ���ʧ�ܣ�");
			temp.add("ɾ���ļ���ʧ�ܣ�");
		}
		return temp;
	}
	
	//�õ�Ŀ¼����
	private static String getDirName(String dir) {
		if (dir.endsWith(File.separator)) { // ����ļ���·����"//"��β������ȥ��ĩβ��"//"
			dir = dir.substring(0, dir.lastIndexOf(File.separator));
		}
		return dir.substring(dir.lastIndexOf(File.separator) + 1);
	}
	
	//�����ļ�
	private boolean copyFile(String srcPath, String destDir) {
		boolean flag = false;
		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // Դ�ļ�������
			System.out.println("Դ�ļ�������");
			return false;
		}
		// ��ȡ�������ļ����ļ���
		String fileName = srcPath
				.substring(srcPath.lastIndexOf(File.separator));
		String destPath = destDir + fileName;
		if (destPath.equals(srcPath)) { // Դ�ļ�·����Ŀ���ļ�·���ظ�
			System.out.println("Դ�ļ�·����Ŀ���ļ�·���ظ�!");
			return false;
		}
		File destFile = new File(destPath);
		if (destFile.exists() && destFile.isFile()) { // ��·�����Ѿ���һ��ͬ���ļ�
			System.out.println("Ŀ��Ŀ¼������ͬ���ļ�!");
			return false;
		}
		File destFileDir = new File(destDir);
		destFileDir.mkdirs();
		try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();
			flag = true;
		} catch (IOException e) {
			//
			System.out.println("Error!");
		}
		return flag;
	}
	
	
	//�����ļ���
	private boolean copyDirectory(String srcPath, String destDir) {
		boolean flag = false;
 
		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // Դ�ļ��в�����
			System.out.println("Դ�ļ��в�����");
			return false;
		}
		// ��ô����Ƶ��ļ��е����֣���������Ƶ��ļ���Ϊ"E://dir"���ȡ������Ϊ"dir"
		String dirName = getDirName(srcPath);
		// Ŀ���ļ��е�����·��
		String destPath = destDir + File.separator + dirName;
		// System.out.println("Ŀ���ļ��е�����·��Ϊ��" + destPath);
 
		if (destPath.equals(srcPath)) {
			System.out.println("Ŀ���ļ�����Դ�ļ����ظ�");
			return false;
		}
		File destDirFile = new File(destPath);
		if (destDirFile.exists()) { // Ŀ��λ����һ��ͬ���ļ���
			System.out.println("Ŀ��λ������ͬ���ļ���!");
			return false;
		}
		destDirFile.mkdirs(); // ����Ŀ¼
		File[] fileList = srcFile.listFiles(); // ��ȡԴ�ļ����µ����ļ������ļ���
		if (fileList.length == 0) { // ���Դ�ļ���Ϊ��Ŀ¼��ֱ������flagΪtrue����һ���ǳ����Σ�debug�˺ܾ�
			flag = true;
		} else {
			for (File temp : fileList) {
				if (temp.isFile()) { // �ļ�
					flag = copyFile(temp.getAbsolutePath(), destPath);
				} else if (temp.isDirectory()) { // �ļ���
					flag = copyDirectory(temp.getAbsolutePath(), destPath);
				}
				if (!flag) {
					break;
				}
			}
		}
		if (flag) {
			System.out.println("�����ļ��гɹ�!");
		}
		return flag;
	}
	
	//�����ļ����ļ���
	public ArrayList<String> copyGeneralFile(String srcPath, String destDir) {
		ArrayList<String> temp = new ArrayList<String>();
		boolean flag = false;
		File file = new File(srcPath);
		if (!file.exists()) {
			//System.out.println("Դ�ļ���Դ�ļ��в�����!");
			temp.add("Դ�ļ���Դ�ļ��в�����!");
		}
		if (file.isFile()) { // Դ�ļ�
			//System.out.println("��������ļ�����!");
			temp.add("��������ļ����ƣ�");
			flag = copyFile(srcPath, destDir);
			if(flag == true) {
				temp.add("�ļ����Ƴɹ���");
			}
			else {
				temp.add("�ļ�����ʧ�ܣ�");
			}
		} else if (file.isDirectory()) {
			//System.out.println("��������ļ��и���!");
			temp.add("��������ļ��и��ƣ�");
			flag = copyDirectory(srcPath, destDir);
			if(flag == true) {
				temp.add("�ļ��и��Ƴɹ���");
			}
			else {
				temp.add("�ļ��и���ʧ�ܣ�");
			}
		}
		return temp;
	}
		
	public ArrayList<String> getFiles(String path) {
	    File file = new File(path);
	    File[] tempList = file.listFiles();
	    ArrayList<String> temp = new ArrayList<String>();
	    for (int i = 0; i < tempList.length; i++) {
	        if (tempList[i].isFile()) {
	        	//System.out.println(tempList[i].getName());
	        	temp.add(tempList[i].getName());
	        }
	        if (tempList[i].isDirectory()) {
	        	//System.out.println(tempList[i].getName());
	        	temp.add(tempList[i].getName());
	        }
	    }
	    //System.out.println(temp);
	    return temp;
	}
	
	//���ļ����м���
	//Stirng fileUrl ��Ҫ���ܵ��ļ���·���磺I:\test2\3.txt
	//int key ��Կ
	public ArrayList<String> encrypt(String fileUrl, int key) throws Exception {
		ArrayList<String> tempArrayList = new ArrayList<String>();
		File file = new File(fileUrl);
		String path = file.getPath();
		//System.out.println(path);
		if(!file.exists()){
			//System.out.println("�ļ������ڣ�");
			tempArrayList.add("�ļ������ڣ�");
			return tempArrayList;
		}
		int index = path.lastIndexOf("\\");
		String destFile = path.substring(0, index)+"\\"+"abc";
		File dest = new File(destFile);
		InputStream in = new FileInputStream(fileUrl);
		OutputStream out = new FileOutputStream(destFile);
		byte[] buffer = new byte[1024];
		int r;
		byte[] buffer2 = new byte[1024];
		while (( r = in.read(buffer)) > 0) {
			for(int i = 0; i < r; i++){
				byte b = buffer[i];
				b = (byte)(b^key);
				buffer2[i] = b;
			}
			out.write(buffer2, 0, r);
			out.flush();
		}
		in.close();
		out.close();
		file.delete();
		dest.renameTo(new File(fileUrl));
		//System.out.println("���ܳɹ�");
		tempArrayList.add("���ܳɹ�");
		return tempArrayList;
	}
	
	//����
	public String decrypt(String fileUrl, String tempUrl, int key) throws Exception{
		File file = new File(fileUrl);
		if (!file.exists()) {
			return "����ʧ�ܣ��ļ�������";
		}
		File dest = new File(tempUrl);
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		InputStream is = new FileInputStream(fileUrl);
		OutputStream out = new FileOutputStream(tempUrl);
		byte[] buffer = new byte[1024];
		byte[] buffer2 =new byte[1024];
		byte bMax = (byte)255;
		long size = file.length();
		int mod = (int) (size%1024);
		int div = (int) (size>>10);
		int count = mod == 0 ? div : (div+1);
		int k = 1, r;
		while ((k <= count && ( r = is.read(buffer)) > 0)) {
			if(mod != 0 && k == count) {
				r =  mod;
			}
			for(int i = 0; i < r; i++){
				byte b = buffer[i];
				b = (byte)(b^key);
				buffer2[i] = b;
			}
			out.write(buffer2, 0, r);
			k++;
		}
		out.close();
		is.close();
		return "�������";
	}
	
	public void showDec() throws Exception {
		System.out.print("��Ҫ���ܵ��ļ���·����");
		Scanner din = new Scanner(System.in);
		String srcpath = din.nextLine();
		System.out.print("���ܺ���ļ���·����");
		String despath = din.nextLine();
		decrypt(srcpath, despath, numOfEncAndDec);
	}

	//��Կ
	private static final int numOfEncAndDec = 20164277;
	
	public String showCd(String systempath, String path) {
		if(path.contains(":")) {
			return path;
		}
		else if(path.equals("..")) {
			int index = systempath.lastIndexOf("\\");
			path = systempath.substring(0, index);
		}
		else {
			path = systempath + "\\" + path;
		}
		return path;
	}
}

