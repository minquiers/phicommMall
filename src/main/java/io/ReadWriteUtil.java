package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadWriteUtil {
	
	public static List<String> read(InputStream is) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"));
        String temp = null;
        List<String> list = new ArrayList<String>();
        while(null != (temp = br.readLine())){
            temp = temp.trim();
            list.add(temp);
        }
        br.close();
        return list;
    }
	
    public static List<String> read(String file) throws Exception{
    	List<String> list = new ArrayList<String>();
    	File f = new File(file);
    	if(!f.exists()){
    		if(f.isDirectory()){
    			f.mkdirs();
    		}else{
    			File parentDir = f.toPath().getParent().toFile();
    			if(!parentDir.exists()){
    				parentDir.mkdirs();
    			}
    			return list;
    		}
    	}
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f) , "UTF-8"));
        String temp = null;
        while(null != (temp = br.readLine())){
            temp = temp.trim();
            list.add(temp);
        }
        br.close();
        return list;
    }
    
    public static String readToString(String dir , String fileName) throws Exception{
    	File file = new File(dir , fileName);
    	if(file.exists()){
    		return readToString(file.getPath());
    	}
    	return null;
    }
    
    public static String readToString(String file) throws Exception{
    	File f = new File(file);
    	if(!f.exists()){
    		if(f.isDirectory()){
    			f.mkdirs();
    		}else{
    			File parentDir = f.toPath().getParent().toFile();
    			if(!parentDir.exists()){
    				parentDir.mkdirs();
    			}
    			return null;
    		}
    	}
        BufferedReader br = new BufferedReader(new FileReader(f));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        while(null != (temp = br.readLine())){
            temp = temp.trim();
            sb.append(temp);
        }
        br.close();
        return sb.toString();
    }
    
    public static void write(String value ,String file) throws Exception{
    	File f = new File(file);
    	if(!f.exists()){
    		if(f.isDirectory()){
    			f.mkdirs();
    		}else{
    			File parentDir = f.toPath().getParent().toFile();
    			if(!parentDir.exists()){
    				parentDir.mkdirs();
    			}
    			f.createNewFile();
    		}
    	}
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(value);
        bw.close();
    }
    
    public static void writeList(List<String> list ,String file) throws Exception{
    	File f = new File(file);
    	if(!f.exists()){
    		if(f.isDirectory()){
    			f.mkdirs();
    		}else{
    			File parentDir = f.toPath().getParent().toFile();
    			if(!parentDir.exists()){
    				parentDir.mkdirs();
    			}
    			f.createNewFile();
    		}
    	}
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        StringBuilder sb = new StringBuilder();
        for(String s : list){
            sb.append(s).append("\r\n");
        }
        bw.write(sb.toString().trim());
        bw.close();
    }
}
