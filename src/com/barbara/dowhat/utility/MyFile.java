package com.barbara.dowhat.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.widget.Toast;

public class MyFile {
	 
	 public static final class SMS implements BaseColumns {
		    public static final Uri INBOX_URI = Uri.parse("content://sms/inbox");
		    
		    public static final String THREAD_ID = "thread_id";
		    public static final String ADDRESS = "address";
		    public static final String SUBJECT = "subject";
		    public static final String BODY = "body";
		  }
	 public static final class Yaml {
		    public static final String DateTimeFormat = "yyyy-MM-dd HH:mm:ss";
		    public static final String shortTime = "yyyyMMdd";
		  }
	 
	 

	public static String read(String filename) { 
		String content = "";       
        int length;
		try {
			File file = new File(filename);
			FileInputStream fin = new FileInputStream(file);
			length = fin.available();
			byte [] buffer = new byte[length];   
	        fin.read(buffer);       
	        content = EncodingUtils.getString(buffer, "UTF-8");   
	        fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
    }
	
	
	public static void saveToYaml(Context context, String dir, String path, String filename, String yamlStr){
		File bakDir = null;//存储路径
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File extStorageDir = new File(path);
			if (extStorageDir.exists() || extStorageDir.mkdirs()) {
				bakDir = extStorageDir;
			}
		}
		if (null == bakDir) {
			bakDir = context.getDir(dir,
					Context.MODE_PRIVATE);
		}
		try {
			FileOutputStream fos = new FileOutputStream(new File(bakDir,
					filename));
			fos.write(yamlStr.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
