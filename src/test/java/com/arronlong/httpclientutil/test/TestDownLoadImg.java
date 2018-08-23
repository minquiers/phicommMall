package com.arronlong.httpclientutil.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.exception.HttpProcessException;

/**
 * 下载demo
 * 
 * @author arron
 * @date 2016年6月7日 上午10:29:30 
 * @version 1.0
 */
public class TestDownLoadImg {
	
	public static void main(String[] args) throws FileNotFoundException, HttpProcessException{
		for(int i = 0 ; i < 100;i++){
		String imgUrl = "index.php/vcode-index-passport.html"; //百度logo
			File file = new File("C:/Users/Administrator/Desktop/tensorflow/captcha/testImages/" + (i + 1) + ".png");
			HttpClientUtil.down(HttpConfig.custom().url(imgUrl).out(new FileOutputStream(file)));
			if (file.exists()) {
				System.out.println("图片下载成功了！存放在：" + file.getPath());
			}
		}
		/*
		String mp3Url="http://win.web.rh01.sycdn.kuwo.cn/resource/n1/24/6/707126989.mp3"; //四叶草-好想你
		file = new File("c:/好想你.mp3");
		HttpClientUtil.down(HttpConfig.custom().url(mp3Url).out(new FileOutputStream(file)));
		if (file.exists()) {
			System.out.println("mp3下载成功了！存放在：" + file.getPath());
		}*/
	}
}
