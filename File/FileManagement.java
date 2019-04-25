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
	//创建文件
	public ArrayList<String> createFile(String path,String fileName) {
		ArrayList<String> temp = new ArrayList<String>();
		String str = path + fileName;
		File file = new File(str);
		if (file.exists()) {
            //System.out.println("该文件已存在。");
			temp.add("该文件已存在。");
        } else {
            //System.out.println("该文件不存在，创建该文件。");
        	temp.add("该文件不存在，创建该文件。");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return temp;
	}
	
	//创建文件夹
	public ArrayList<String> createFolder(String path, String folderName){
		ArrayList<String> temp = new ArrayList<String>();
		String str = path + folderName;
		File dir = new File(str);
		if (dir.exists()) {
            if (dir.isDirectory()) {
                //System.out.println("该文件夹已存在。");
            	temp.add("该文件夹已存在。");
            } else {
                //System.out.println("有相同名字的文件夹存在，不能创建。");
            	temp.add("有相同名字的文件夹存在，不能创建。");
            }
        } else {
            //System.out.println("该文件夹不存在，创建该文件夹。");
            temp.add("该文件夹不存在，创建该文件夹。");
            dir.mkdirs();
        }
		return temp;
	}
	
	//删除文件
	public ArrayList<String> deleteFile(String path, String fileName) {
		ArrayList<String>  temp = new ArrayList<String>();
		String str = path + fileName;
		File file = new File(str);
		if (file.exists()) {
            //文件存在
            //System.out.println(file.getAbsolutePath()+"存在。");
            String tString = file.getAbsolutePath() + "存在";
            temp.add(tString);
            file.delete();
            //System.out.println("该文件已删除。");
            temp.add("该文件已删除。");
        } else {
            //文件压根不存在
            //System.out.println("文件不存在。");
        	temp.add("文件不存在。");
        }
		return temp;
	}
	
	//删除文件夹
	public ArrayList<String> deleteFolder(String path, String folderName) {
		boolean flag = true;
		ArrayList<String> temp = new ArrayList<String>();
		String str = path + folderName;
		File dirfile = new File(str);
		if(!dirfile.isDirectory()) {
			flag = true;
			//System.out.println("文件夹不存在!");
			temp.add("文件夹不存在！");
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
			//System.out.println("删除文件夹成功！");
			temp.add("删除文件夹成功！");
		}
		else {
			//System.out.println("删除文件夹失败！");
			temp.add("删除文件夹失败！");
		}
		return temp;
	}
	
	//得到目录名字
	private static String getDirName(String dir) {
		if (dir.endsWith(File.separator)) { // 如果文件夹路径以"//"结尾，则先去除末尾的"//"
			dir = dir.substring(0, dir.lastIndexOf(File.separator));
		}
		return dir.substring(dir.lastIndexOf(File.separator) + 1);
	}
	
	//复制文件
	private boolean copyFile(String srcPath, String destDir) {
		boolean flag = false;
		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件不存在
			System.out.println("源文件不存在");
			return false;
		}
		// 获取待复制文件的文件名
		String fileName = srcPath
				.substring(srcPath.lastIndexOf(File.separator));
		String destPath = destDir + fileName;
		if (destPath.equals(srcPath)) { // 源文件路径和目标文件路径重复
			System.out.println("源文件路径和目标文件路径重复!");
			return false;
		}
		File destFile = new File(destPath);
		if (destFile.exists() && destFile.isFile()) { // 该路径下已经有一个同名文件
			System.out.println("目标目录下已有同名文件!");
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
	
	
	//复制文件夹
	private boolean copyDirectory(String srcPath, String destDir) {
		boolean flag = false;
 
		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件夹不存在
			System.out.println("源文件夹不存在");
			return false;
		}
		// 获得待复制的文件夹的名字，比如待复制的文件夹为"E://dir"则获取的名字为"dir"
		String dirName = getDirName(srcPath);
		// 目标文件夹的完整路径
		String destPath = destDir + File.separator + dirName;
		// System.out.println("目标文件夹的完整路径为：" + destPath);
 
		if (destPath.equals(srcPath)) {
			System.out.println("目标文件夹与源文件夹重复");
			return false;
		}
		File destDirFile = new File(destPath);
		if (destDirFile.exists()) { // 目标位置有一个同名文件夹
			System.out.println("目标位置已有同名文件夹!");
			return false;
		}
		destDirFile.mkdirs(); // 生成目录
		File[] fileList = srcFile.listFiles(); // 获取源文件夹下的子文件和子文件夹
		if (fileList.length == 0) { // 如果源文件夹为空目录则直接设置flag为true，这一步非常隐蔽，debug了很久
			flag = true;
		} else {
			for (File temp : fileList) {
				if (temp.isFile()) { // 文件
					flag = copyFile(temp.getAbsolutePath(), destPath);
				} else if (temp.isDirectory()) { // 文件夹
					flag = copyDirectory(temp.getAbsolutePath(), destPath);
				}
				if (!flag) {
					break;
				}
			}
		}
		if (flag) {
			System.out.println("复制文件夹成功!");
		}
		return flag;
	}
	
	//复制文件或文件夹
	public ArrayList<String> copyGeneralFile(String srcPath, String destDir) {
		ArrayList<String> temp = new ArrayList<String>();
		boolean flag = false;
		File file = new File(srcPath);
		if (!file.exists()) {
			//System.out.println("源文件或源文件夹不存在!");
			temp.add("源文件或源文件夹不存在!");
		}
		if (file.isFile()) { // 源文件
			//System.out.println("下面进行文件复制!");
			temp.add("下面进行文件复制！");
			flag = copyFile(srcPath, destDir);
			if(flag == true) {
				temp.add("文件复制成功！");
			}
			else {
				temp.add("文件复制失败！");
			}
		} else if (file.isDirectory()) {
			//System.out.println("下面进行文件夹复制!");
			temp.add("下面进行文件夹复制！");
			flag = copyDirectory(srcPath, destDir);
			if(flag == true) {
				temp.add("文件夹复制成功！");
			}
			else {
				temp.add("文件夹复制失败！");
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
	
	//对文件进行加密
	//Stirng fileUrl 需要加密的文件的路径如：I:\test2\3.txt
	//int key 密钥
	public ArrayList<String> encrypt(String fileUrl, int key) throws Exception {
		ArrayList<String> tempArrayList = new ArrayList<String>();
		File file = new File(fileUrl);
		String path = file.getPath();
		//System.out.println(path);
		if(!file.exists()){
			//System.out.println("文件不存在！");
			tempArrayList.add("文件不存在！");
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
		//System.out.println("加密成功");
		tempArrayList.add("加密成功");
		return tempArrayList;
	}
	
	//解密
	public String decrypt(String fileUrl, String tempUrl, int key) throws Exception{
		File file = new File(fileUrl);
		if (!file.exists()) {
			return "解密失败，文件不存在";
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
		return "解密完成";
	}
	
	public void showDec() throws Exception {
		System.out.print("需要解密的文件的路径：");
		Scanner din = new Scanner(System.in);
		String srcpath = din.nextLine();
		System.out.print("解密后的文件的路径：");
		String despath = din.nextLine();
		decrypt(srcpath, despath, numOfEncAndDec);
	}

	//密钥
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

