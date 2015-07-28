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
public class EmailVerifyModel {

    public String Email;
    public String verifyCode;
    public long putDate;

    public EmailVerifyModel(String pEmail, String pVerifyCode) {
        Email = pEmail;
        verifyCode = pVerifyCode;
        putDate = new Date().getTime();
    }
}
