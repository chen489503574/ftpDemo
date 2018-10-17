package com;

import org.apache.commons.net.ftp.*;

import java.io.*;

/**
 * Created by admin on 2018/8/1.
 */
public class ftpTest {
//    public static void main(String[] args) {
//        testUpload();
////        testDownload();
//    }
//    /**
//     * FTP上传单个文件测试
//     */
//    public static void testUpload() {
//        FTPClient ftpClient = new FTPClient();
//        FileInputStream fis = null;
//        try {
//            ftpClient.connect("172.16.5.27");
////            ftpClient.login("admin", "123");
//
//            File srcFile = new File("F:/11.jpg");//本地上传文件位置
//            fis = new FileInputStream(srcFile);
//            //设置上传目录
//            ftpClient.changeWorkingDirectory("G:/FTPsave");//FTP服务器存储位置
//            ftpClient.setBufferSize(1024);
//            ftpClient.setControlEncoding("GBK");
//            //设置文件类型（二进制位）
//            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//            ftpClient.storeFile("11.jpg", fis);
//            System.out.println("上传单个文件完成！！！");
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("FTP用户端出错！", e);
//
//        } finally {
//            IOUtils.closeQuietly(fis);
//            try {
//                ftpClient.disconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new RuntimeException("关闭FTP连接发生异常！", e);
//            }
//        }
//    }
//    /**
//     * FTP下载单个文件测试
//     */
//    public static void testDownload() {
//        FTPClient ftpClient = new FTPClient();
//        FileOutputStream fos = null;
//        try {
//            ftpClient.connect("172.16.5.27");
////            ftpClient.login("admin", "123");
//            String remoteFileName = "G:/FTPsave/haipei.log.tar.gz.rar";//下载本地位置
//            fos = new FileOutputStream("F:/haipei.log.tar.gz.rar");//FTP服务器文件位置
//            ftpClient.setBufferSize(1024);
//            //设置文件类型（二进制位）
//            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//            ftpClient.retrieveFile(remoteFileName, fos);
//            System.out.println("下载文件完成！！！");
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("FTP用户端出错！", e);
//        } finally {
//            IOUtils.closeQuietly(fos);
//            try {
//                ftpClient.disconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new RuntimeException("关闭FTP连接发生异常！", e);
//            }
//        }
//    }
    /**
     * 上传文件-FTP方式
     * @param ftp FTPClient对象
     * @param path FTP服务器上传地址
     * @param filename 本地文件路径
     * @param inputStream 输入流
     * @return boolean
     */
    public boolean uploadFile(FTPClient ftp, String path, String fileName, InputStream inputStream) {
        boolean success = false;
        try {
            ftp.changeWorkingDirectory(path);//上传到指定FTP服务器目录
            FTPFile[] fs = ftp.listFiles();//得到目录的相应文件列表
            fileName = ftpTest.changeName(fileName, fs);//根据重名判断的结果 生成新的文件的名称
            fileName = new String(fileName.getBytes("GBK"),"ISO-8859-1");
            path = new String(path.getBytes("GBK"), "ISO-8859-1");
            //转到指定上传目录
            ftp.changeWorkingDirectory(path);
            //将上传文件存储到指定目录
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //如果缺省该句 传输txt正常 但图片和其他格式的文件传输出现乱码
            ftp.storeFile(fileName, inputStream);
            //关闭输入流
            inputStream.close();
            //退出ftp
            ftp.logout();
            //表示上传成功
            success = true;
            System.out.println("上传成功。。。。。。");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
    /**
     * 下载文件-FTP方式
     * @param ftp FTPClient对象
     * @param path FTP服务器上传地址
     * @param fileName 本地文件路径
     * @param localPath 本里存储路径
     * @return boolean
     */
    public boolean downFile(FTPClient ftpClient, String path, String fileName, String localPath) {
        boolean success = false;
        try {
            ftpClient.changeWorkingDirectory(path);//转移到FTP服务器目录
            FTPFile[] fs = ftpClient.listFiles(); //得到目录的相应文件列表
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    File localFile = new File(localPath + "\\" + ff.getName());
                    OutputStream outputStream = new FileOutputStream(localFile);
                    //将文件保存到输出流outputStream中
                    ftpClient.retrieveFile(new String(ff.getName().getBytes("GBK"), "ISO-8859-1"), outputStream);
                    outputStream.flush();
                    outputStream.close();
                    System.out.println("下载成功");
                }
            }
            ftpClient.logout();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 删除文件-FTP方式
     * @param ftp FTPClient对象
     * @param path FTP服务器上传地址
     * @param filename FTP服务器上要删除的文件名
     * @return
     */
    public boolean deleteFile(FTPClient ftp, String ftpPath, String fileName) {
        boolean success = false;
        try {
            ftp.changeWorkingDirectory(ftpPath);//转移到指定FTP服务器目录
            fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
            ftpPath = new String(ftpPath.getBytes("GBK"), "ISO-8859-1");
            ftp.deleteFile(fileName);
            ftp.logout();
            success = true;
            System.out.println("删除文件成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 获得连接-FTP方式
     * @param hostname FTP服务器地址
     * @param port FTP服务器端口
     * @param username FTP登录用户名
     * @param password FTP登录密码
     * @return FTPClient
     */
    public FTPClient getConnectionFTP(String hostName, int port, String userName, String passWord) {
        //创建FTPClient对象
        FTPClient ftp = new FTPClient();
        try {
            //连接FTP服务器
            ftp.connect(hostName, port);
            //下面三行代码必须要，而且不能改变编码格式，否则不能正确下载中文文件
            ftp.setControlEncoding("GBK");
            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
            conf.setServerLanguageCode("zh");
            //登录ftp
            ftp.login(userName, passWord);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                ftp.disconnect();
                System.out.println("连接ftp服务器失败");
            }else {
                System.out.println("登陆服务器成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftp;
    }

    /**
     * 关闭连接-FTP方式
     * @param ftp FTPClient对象
     * @return boolean
     */
    public boolean closeFTP(FTPClient ftp) {
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
                System.out.println("ftp已经关闭");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 判断是否有重名文件
     * @param fileName
     * @param fs
     * @return
     */
    public static boolean isFileExist(String fileName, FTPFile[] fs) {
        for (int i = 0; i < fs.length; i++) {
            FTPFile ff = fs[i];
            if (ff.getName().equals(fileName)) {
                return true; //如果存在返回 正确信号
            }
        }
        return false; //如果不存在返回错误信号
    }

    /**
     * 根据重名判断的结果 生成新的文件的名称
     * @param fileName
     * @param fs
     * @return
     */
    public static String changeName(String fileName, FTPFile[] fs) {
        int n = 0;
//      fileName = fileName.append(fileName);
        while (isFileExist(fileName.toString(), fs)) {
            n++;
            String a = "[" + n + "]";
            int b = fileName.lastIndexOf(".");//最后一出现小数点的位置
            int c = fileName.lastIndexOf("[");//最后一次"["出现的位置
            if (c < 0) {
                c = b;
            }
            StringBuffer name = new StringBuffer(fileName.substring(0, c));//文件的名字
            StringBuffer suffix = new StringBuffer(fileName.substring(b + 1));//后缀的名称
            fileName = name.append(a) + "." + suffix;
        }
        return fileName.toString();
    }

    /**
     * @param args
     * @throws FileNotFoundException
     * 测试程序
     */
    public static void main(String[] args) throws FileNotFoundException {
        testUploadFile("11.jpg");
        testDownFile("33.jpg");
//        testDeleteFile("11.jpg");

    }

    /**
     * 上传测试方法
     * @throws FileNotFoundException
     */
    public static void testUploadFile(String file)throws FileNotFoundException{
        String path = "G:\\FTPsave\\";
        File f1 = new File("F:\\"+file);
        String filename = f1.getName();
        System.out.println("上传的文件为："+filename);
        ftpTest ftpTest = new ftpTest();
        InputStream input = new FileInputStream(f1);
        //连接FTP服务器
        FTPClient ftp = ftpTest.getConnectionFTP("172.16.5.128", 21, "admin", "4569");
        ftpTest.uploadFile(ftp, path, filename, input);
        ftpTest.closeFTP(ftp);
    }

    /**
     * 下载测试方法
     * @throws FileNotFoundException
     */
    public static void testDownFile(String file)throws FileNotFoundException{
        String ftpPath ="G:\\FTPsave\\";
        String localPath = "F:\\";
        String filename = new File("G:\\FTPsave\\"+file).getName();
        System.out.println("下载的文件为："+filename);
        ftpTest ftpTest = new ftpTest();
        //连接FTP服务器
        FTPClient ftp = ftpTest.getConnectionFTP("172.16.5.128", 21, "admin", "4569");
        ftpTest.downFile(ftp, ftpPath, filename,localPath);
        ftpTest.closeFTP(ftp);
    }

    /**
     * 删除测试方法-FTP方式
     * @throws FileNotFoundException
     */
    public static void testDeleteFile(String file)throws FileNotFoundException{
        String ftpPath ="G:\\FTPsave\\";
        String filename = new File("G:\\FTPsave\\"+file).getName();
        System.out.println("删除的文件为："+filename);
        ftpTest ftpTest = new ftpTest();
        //连接FTP服务器
        FTPClient ftp = ftpTest.getConnectionFTP("172.16.5.128", 21, "admin", "4569");
        ftpTest.deleteFile(ftp, ftpPath, filename);
        ftpTest.closeFTP(ftp);
    }

}
