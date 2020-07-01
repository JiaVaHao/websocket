package com.jwh.dobotochatserver.util;

import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class DFileUtil {
    //创建文件
    public boolean createNewFile(String filePath, String fileName) {
        String pathname = filePath + fileName;
        File file = new File(pathname);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    //删除文件
    public boolean deleteFile(String filePath, String fileName) {
        String pathname = filePath + fileName;
        File file = new File(pathname);
        if (!file.exists()) {
            return file.delete();
        }
        return true;
    }

    //从文件中读出内容
    public String readFile(String filePath, String fileName) throws IOException {
        String pathname = filePath + fileName;
        File file = new File(pathname);
        if (!file.exists()) {
            return "文件不存在";
        } else {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bReader = new BufferedReader(reader);
            StringBuffer sb = new StringBuffer();
            String text = null;
            while ((text = bReader.readLine()) != null) {
                sb.append(text);
            }
            bReader.close();
            reader.close();
            return sb.toString();
        }
    }

    //将内容写入文件
    public String writeFile(String filePath, String fileName, String data) throws IOException {
        String pathname = filePath + fileName;
        File file = new File(pathname);
        if (!file.exists()) {
            return "文件不存在";
        } else {
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bWriter = new BufferedWriter(fileWriter);
            bWriter.write(data);
            bWriter.close();
            fileWriter.close();
            return "文件改写成功";
        }
    }
}
