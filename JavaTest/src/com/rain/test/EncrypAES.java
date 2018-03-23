package com.rain.test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class EncrypAES {
	//KeyGenerator �ṩ�Գ���Կ�������Ĺ��ܣ�֧�ָ����㷨  
    private KeyGenerator keygen;  
    //SecretKey ���𱣴�Գ���Կ  
    private SecretKey deskey;  
    //Cipher������ɼ��ܻ���ܹ���  
    private Cipher cipher;  
    //���ֽ����鸺�𱣴���ܵĽ��  
    private byte[] cipherByte;  
      
    @SuppressWarnings("restriction")
	public EncrypAES() throws NoSuchAlgorithmException, NoSuchPaddingException{  
        Security.addProvider(new com.sun.crypto.provider.SunJCE());  
        //ʵ����֧��DES�㷨����Կ������(�㷨���������谴�涨�������׳��쳣)  
        keygen = KeyGenerator.getInstance("AES");  
        //������Կ  
        deskey = keygen.generateKey();  
        //����Cipher����,ָ����֧�ֵ�DES�㷨  
        cipher = Cipher.getInstance("AES");  
    }  
      
    /** 
     * ���ַ������� 
     *  
     * @param str 
     * @return 
     * @throws InvalidKeyException 
     * @throws IllegalBlockSizeException 
     * @throws BadPaddingException 
     */  
    public byte[] Encrytor(String str) throws InvalidKeyException,  
            IllegalBlockSizeException, BadPaddingException {  
        // ������Կ����Cipher������г�ʼ����ENCRYPT_MODE��ʾ����ģʽ  
        cipher.init(Cipher.ENCRYPT_MODE, deskey);  
        byte[] src = str.getBytes();  
        // ���ܣ���������cipherByte  
        cipherByte = cipher.doFinal(src);  
        return cipherByte;  
    }  
  
    /** 
     * ���ַ������� 
     *  
     * @param buff 
     * @return 
     * @throws InvalidKeyException 
     * @throws IllegalBlockSizeException 
     * @throws BadPaddingException 
     */  
    public byte[] Decryptor(byte[] buff) throws InvalidKeyException,  
            IllegalBlockSizeException, BadPaddingException {  
        // ������Կ����Cipher������г�ʼ����DECRYPT_MODE��ʾ����ģʽ  
        cipher.init(Cipher.DECRYPT_MODE, deskey);  
        cipherByte = cipher.doFinal(buff);  
        return cipherByte;  
    }  
  
    /** 
     * @param args 
     * @throws NoSuchPaddingException  
     * @throws NoSuchAlgorithmException  
     * @throws BadPaddingException  
     * @throws IllegalBlockSizeException  
     * @throws InvalidKeyException  
     */  
    public static void testAES() {
    	try {
        EncrypAES de1 = new EncrypAES();  
        String msg ="������ӭ����";  
        byte[] encontent = de1.Encrytor(msg);  
        byte[] decontent = de1.Decryptor(encontent);  
        System.out.println("������:" + msg);  
        System.out.println("���ܺ�:" + new String(encontent));  
        System.out.println("���ܺ�:" + new String(decontent));
    	} catch (Exception ex) {
    		System.out.println("test AES failed");
    	}
    }
}
