/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.JMailVerify;

import java.util.Date;

/**
 *
 * @author Administrator
 */
public class SyncPutEMailVerifyCode implements Runnable {

    public SyncPutEMailVerifyCode(EmailVerifyModel pVerifyModel) {
        verifyModel = pVerifyModel;
    }

    private EmailVerifyModel verifyModel = null;

    @Override
    public void run() {
        synchronized (EmailCommon.emailVerifys) {
            for (EmailVerifyModel evm1 : EmailCommon.emailVerifys) {
                if (evm1.Email.equals(verifyModel.Email)) {
                    evm1.putDate = new Date().getTime();
                    evm1.verifyCode = verifyModel.verifyCode;
                    return;
                }
            }
            EmailCommon.emailVerifys.add(verifyModel);
        }
    }

}
