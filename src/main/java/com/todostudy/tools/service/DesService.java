package com.todostudy.tools.service;

/***
 * Cipher创建的第一种方式中，参数一般称为转换名，转换名由"算法/模式/填充"构成。Java平台的每个实现都需要支持以下标准Cipher转换：
 * - `AES/CBC/NoPadding` （128）
 * - `AES/CBC/PKCS5Padding` （128）
 * - `AES/ECB/NoPadding` （128）
 * - `AES/ECB/PKCS5Padding` （128）
 * - `DES/CBC/NoPadding` （56）
 * - `DES/CBC/PKCS5Padding（56）`
 * - `DES/ECB/NoPadding（56）`
 * - `DES/ECB/PKCS5Padding` （56）
 * - `DESede/CBC/NoPadding` （168）
 * - `DESede/CBC/PKCS5Padding` （168）
 * - `DESede/ECB/NoPadding` （168）
 * - `DESede/ECB/PKCS5Padding` （168）
 * - `RSA/ECB/PKCS1Padding` （ `1024，2048` ）
 * - `RSA/ECB/OAEPWithSHA-1AndMGF1Padding` （ `1024，2048` ）
 * - `RSA/ECB/OAEPWithSHA-256AndMGF1Padding` （ `1024，2048` ）
 */

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

/**
 * @author hanson
 * ，Cipher对象创建的方式有如下两种：
 * Cipher c = Cipher.getInstance("算法/模式/填充");或Cipher c = Cipher.getInstance("算法")
 *   // 得到Cipher实例  CBC是工作模式，DES一共有电子密码本模式（ECB）、加密分组链接模式（CBC）、加密反馈模式（CFB）和输出反馈模式（OFB）四种模式，
 *             cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
 *加密测试用例里只改动一行，改动获取Cipher的方式改为：
 *
 * Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
 * 运行加密测试用例多次，发现每次加密结果都不一样，是因为初始向量IV由于加密时我们没有指定，所以会每次加密的时候系统随机生成IV，
 * 将每次加密后的值都拿去解密，发现全部解密失败，因为解密时需要IV值却不知道IV值是什么。所以程序需要进一步改进为加密时指定IV值，以方便解密时使用同样的IV值来解密。
 * how to use @Import(DesService.class) or @Bean
 */
public class DesService {

    @Value("${han.tools.desKey}")
    private String desKey;
    private Key key;// 密钥的key值
    private byte[] DESkey;
    private byte[] DESIV = { 0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB,
            (byte) 0xCD, (byte) 0xEF };
    private AlgorithmParameterSpec iv = null;// 加密算法的参数接口

    @PostConstruct
    public void init() {
        try {
            this.DESkey = desKey.getBytes("UTF-8");// 设置密钥 大于=8位
            DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
            iv = new IvParameterSpec(DESIV);// 设置向量
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
            key = keyFactory.generateSecret(keySpec);// 得到密钥对象
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密String 明文输入密文输出
     *
     * @param inputString
     *            待加密的明文
     * @return 加密后的字符串
     */
    public String getEnc(String inputString) {
        byte[] byteMi = null;
        byte[] byteMing = null;
        String outputString = "";
        try {
            byteMing = inputString.getBytes("UTF-8");
            byteMi = this.getEncCode(byteMing);
            byte[] temp = Base64.getEncoder().encode(byteMi);
            outputString = new String(temp);
        } catch (Exception e) {
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return outputString;
    }

    /**
     * 解密String 以密文输入明文输出
     *
     * @param inputString
     *            需要解密的字符串
     * @return 解密后的字符串
     */
    public String getDec(String inputString) {
        byte[] byteMing = null;
        byte[] byteMi = null;
        String strMing = "";
        try {
            byteMi = Base64.getDecoder().decode(inputString.getBytes());
            byteMing = this.getDesCode(byteMi);
            strMing = new String(byteMing, "UTF8");
        } catch (Exception e) {
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return strMing;
    }

    /**
     * 加密以byte[]明文输入,byte[]密文输出
     *
     * @param bt
     *            待加密的字节码
     * @return 加密后的字节码
     */
    private byte[] getEncCode(byte[] bt) {
        byte[] byteFina = null;
        Cipher cipher;
        try {
            // 得到Cipher实例  CBC是工作模式，DES一共有电子密码本模式（ECB）、加密分组链接模式（CBC）、加密反馈模式（CFB）和输出反馈模式（OFB）四种模式，
            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byteFina = cipher.doFinal(bt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    /**
     * 解密以byte[]密文输入,以byte[]明文输出
     *
     * @param bt
     *            待解密的字节码
     * @return 解密后的字节码
     */
    private byte[] getDesCode(byte[] bt) {
        Cipher cipher;
        byte[] byteFina = null;
        try {
            // 得到Cipher实例
            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byteFina = cipher.doFinal(bt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cipher = null;
        }
        return byteFina;
    }


}
