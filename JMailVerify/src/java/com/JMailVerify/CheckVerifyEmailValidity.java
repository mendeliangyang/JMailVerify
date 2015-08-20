/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.JMailVerify;

import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author Administrator
 */
public class CheckVerifyEmailValidity implements Runnable {

    @Override
    public void run() {
        synchronized (EmailCommon.emailVerifys) {
            Iterator iterator = EmailCommon.emailVerifys.iterator();
            while (iterator.hasNext()) {
                EmailVerifyModel next = (EmailVerifyModel) iterator.next();
                if (new Date().getTime() - next.putDate >= (1000 * EmailCommon.systemSetModel.EmailVerifyTimeOut)) {
                    EmailCommon.emailVerifys.remove(next);
                }
            }
        }
    }

}
