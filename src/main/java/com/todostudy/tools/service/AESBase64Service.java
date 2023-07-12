package com.todostudy.tools.service;

import com.todostudy.tools.fm.PC;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author hanson
 */
public class AESBase64Service {

    private AESBase64Service(){}
    @Setter
    public String sKey;// 必须16位

    @Setter
    private String IV;//也是用16位
    public AESBase64Service(String skey,String IV){
        this.sKey = skey;
        this.IV = IV;
    }

    public String encrypt(String sSrc) throws Exception {

        // 判断Key是否为16位
        if (sKey.length() != 16) {
            return null;
        }
        byte[] raw = sKey.getBytes(PC.UTF8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, PC.AES);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        //CBC模式需要配置偏移量，设置一个向量，达到密码唯一性，增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(PC.UTF8));

        return Base64.getEncoder().encodeToString(encrypted);
    }

    // 解密
    public String decrypt(String sSrc) throws Exception {
        try {

            // 判断Key是否为16位
            if (sKey.length() != 16) {
                return null;
            }
            byte[] raw = sKey.getBytes(PC.UTF8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, PC.AES);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //CBC模式需要配置偏移量，设置这个后，不会出来同一个明文加密为同一个密文的问题，达到密文唯一性
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.getDecoder().decode(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, PC.UTF8);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }


}
