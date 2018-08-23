package os;

import javax.swing.filechooser.FileSystemView;

public class DeskTopUtil {
	
	public static String getDeskTop(){
	    return FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\";
	}
	
	public static void main(String[] args) {
        System.out.println(getDeskTop());
    }
	
}
