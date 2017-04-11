package util;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.stage.FileChooser;

public class FileChooserUtil {
	private static Desktop desktop;

	public void getDestop() {
		if (desktop == null) {
			desktop = Desktop.getDesktop();
		}
	}

	public static void configureFileChooser(FileChooser fileChooser) {
		fileChooser.setTitle("添加图片");
		// 这里添加了三个选项,分别是名为All images ,其后缀要求是. ,其他类似;
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("所有图片", "*.png","*.jpg","*.jpeg","*.bmp","*.gif","*.svg"));
				
	}

	public static void openFile(File file) {
		EventQueue.invokeLater(() -> {
			try {
				desktop.open(file);
			} catch (IOException e) {
				LogUtil.e(FileChooser.class.getName(), "打开文件选择器出错");
			}
		});
	}
}
