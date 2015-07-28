/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.JMailVerify;

import java.util.*;
//import snaq.db.ConnectionPool;

/**
 *
 * @author Administrator
 */
public class EmailSender {

    /**
     * 验证输入的验证码和服务的验证码是否相同
     *
     * @param to 邮箱地址
     * @param code 验证码
     * @return
     */
    public static boolean verifyCode(String to, String code) {

        EmailVerifyModel tempEvm = null;
        try {
            for (EmailVerifyModel evm1 : EmailCommon.emailVerifys) {
                if (evm1.Email.equals(to)) {
                    tempEvm = evm1;
                    break;
                }
            }
            if (tempEvm == null) {
                return false;
            }
            if (!tempEvm.verifyCode.equals(code)) {
                return false;
            }
            return new Date().getTime() - tempEvm.putDate < (1000 * EmailCommon.systemSetModel.EmailVerifyTimeOut);
        } catch (Exception e) {
            EmailCommon.logError("verifyCode error." + e.getLocalizedMessage(), e);
            return false;
        } finally {
            if (tempEvm != null) {
                synchronized (EmailCommon.emailVerifys) {
                    EmailCommon.emailVerifys.remove(tempEvm);
                }
            }
        }

    }

    /**
     * 发送验证码到指定邮箱 （验证码该方法自动生成）
     *
     * @param to 邮箱地址
     */
    public static void sendVerifyEmail(String to) {

        // 1,产生验证码
        String verifyCode = EmailCommon.getVerifyCode();
        
        EmailVerifyModel model = new EmailVerifyModel(to, verifyCode);
        // 2.更新数据库验证信息
        EmailCommon.putVerifyModel(model);

        // 3.发送验证码到邮箱
        // return sendEmail(systemSetModel.EmailMaster, systemSetModel.EmailMasterPwd, to, systemSetModel.EmailHost, title, verifyCode);
        EmailCommon.asyncSendVerifyEmail(model);
    }

}
