/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.JMailVerify;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Administrator
 */
public class EmailCommon {

    public static boolean initialEmailVerify() {

        // 1 read deploy
        boolean readDeployParamFlag = ReadDeployInformation();
        //2 start up check validity thread pool to  clear up invalid verify email
        startUpCheckEmailValidityPool();
        return readDeployParamFlag;
        //  initial db pool。 替换本地缓存。无须数据库
        // return initializePool();
    }

    public static Set<EmailVerifyModel> emailVerifys = new HashSet<EmailVerifyModel>();

    public static String DeployRootPath = null;
    public static SystemSetModel systemSetModel = null;
    public final static ExecutorService emailSendThreadPool = Executors.newFixedThreadPool(7);
    public final static ExecutorService emailPutThreadPool = Executors.newSingleThreadExecutor();
    public final static ExecutorService emailCheeckValidtiyPool = Executors.newScheduledThreadPool(1);

    /**
     * sync putverifyModel single thread
     *
     * @param model
     */
    public static void putVerifyModel(EmailVerifyModel model) {
        emailPutThreadPool.execute(new SyncPutEMailVerifyCode(model));
    }

    public static void asyncSendVerifyEmail(EmailVerifyModel mode) {
        emailSendThreadPool.execute(new SendVerifyCodeEmail(mode));
    }

    private static void startUpCheckEmailValidityPool() {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new CheckVerifyEmailValidity(), 10, 10, TimeUnit.MINUTES);
    }

    static Random random = new Random();

    /**
     * 生成验证码（6位随机数）
     *
     * @return
     */
    public static String getVerifyCode() {
        return String.format("%06d", random.nextInt(999999));
    }

    private static String DoGetDelplyRootPath() {

        DeployRootPath = EmailSender.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        DeployRootPath = DeployRootPath.substring(1, DeployRootPath.indexOf("WEB-INF"));
        DeployRootPath = new StringBuffer().append(File.separator).append(DeployRootPath).toString();

        return DeployRootPath;
    }

    public static short overrideParseShort(String strShort) {
        if (strShort != null && !strShort.isEmpty()) {
            return Short.parseShort(strShort);
        }
        return -1;
    }

    private static boolean ReadDeployInformation() {
        DocumentBuilderFactory dbFactory = null;
        DocumentBuilder dBuilder = null;
        Document doc = null;
        NodeList systemNodelist = null;
        Element tempSet = null;
        systemSetModel = new SystemSetModel();
        try {
            DoGetDelplyRootPath();
            String deployFile = new StringBuffer().append(DeployRootPath).append(File.separator).append("WEB-INF")
                    .append(File.separator).append("deployInformation.xml").toString();

            dbFactory = DocumentBuilderFactory.newInstance();

            dBuilder = dbFactory.newDocumentBuilder();

            doc = dBuilder.parse(deployFile);

//			systemNodelist = doc.getElementsByTagName("systemSet");
//
//			for (int i = 0; i < systemNodelist.getLength(); i++) {
//				tempSet = (Element) systemNodelist.item(i);
//				systemSetModel.id = tempSet.getAttribute("id");
//				systemSetModel.dbAddress = tempSet.getElementsByTagName("dbAddress").item(0).getTextContent();
//				systemSetModel.dbName = tempSet.getElementsByTagName("dbName").item(0).getTextContent();
//				systemSetModel.dbUser = tempSet.getElementsByTagName("dbUser").item(0).getTextContent();
//				systemSetModel.dbPwd = tempSet.getElementsByTagName("dbpwd").item(0).getTextContent();
//				systemSetModel.dbPort = tempSet.getElementsByTagName("dbPort").item(0).getTextContent();
//			}
            systemSetModel.EmailMaster = doc.getElementsByTagName("EmailMaster").item(0).getTextContent();
            systemSetModel.EmailMasterPwd = doc.getElementsByTagName("EmailMasterPwd").item(0).getTextContent();
            systemSetModel.EmailHost = doc.getElementsByTagName("EmailHost").item(0).getTextContent();
            systemSetModel.EmailTitle = doc.getElementsByTagName("EmailTitle").item(0).getTextContent();
            systemSetModel.EmailVerifyTimeOut = overrideParseShort(
                    doc.getElementsByTagName("EmailVerifyTimeOut").item(0).getTextContent());
            logInfo("read deploy information success.");
            return true;
        } catch (Exception e) {
            logError("read deploy information error " + e.getLocalizedMessage(), e);
            return false;
        } finally {
            dbFactory = null;
            dBuilder = null;
            doc = null;
            systemNodelist = null;
            tempSet = null;
        }

    }

    // TODO write log
    public static void logInfo(String str) {
        System.out.print(str);
    }

    // TODO write log
    public static void logError(String str) {
        System.err.print(str);
    }

    // TODO write log
    public static void logError(String str, Exception e) {
        System.err.print(str);
    }

    // public static boolean initializePool() {
    // try {
    // StringBuffer temp = new StringBuffer();
    // Class c = Class.forName("com.sybase.jdbc3.jdbc.SybDriver"); // Fill JDBC
    // driver class name here.
    // Driver driver = (Driver) c.newInstance();
    // DriverManager.registerDriver(driver);
    //// if (tempSystemSet.id.equals("ElectornicBank") ||
    // tempSystemSet.id.equals("microCredit")) {
    // temp.delete(0, temp.length());
    // // Use the Sybase jConnect driver...
    // temp.append("jdbc:sybase:Tds:");
    // // to connect to the supplied machine name...
    // temp.append(systemSetModel.dbAddress);
    // // on the default port number for ASA...
    // temp.append(":");
    // temp.append(systemSetModel.dbPort);
    // temp.append("/");
    // //temp.append(":5000/");
    // temp.append(systemSetModel.dbName);
    // temp.append("?ServiceName=");
    // temp.append(systemSetModel.dbName);
    // //temp.append("?language=us_english&charset=cp936");
    // // 1:pool-name,2:min,3:max,4:size,5:timeout,6:url,7:name,8:passwd
    // dbPool = new ConnectionPool(systemSetModel.id, 5, 5, 6, 4000,
    // temp.toString(), systemSetModel.dbUser, systemSetModel.dbPwd);
    // dbPool.setAsyncDestroy(true);
    // dbPool.setCaching(false);
    //
    // return true;
    // } catch (Exception e) {
    // logError("initial db pool error " + e.getLocalizedMessage(), e);
    // return false;
    // }
    //
    // }
}
