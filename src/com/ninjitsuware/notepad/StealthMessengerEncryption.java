package com.ninjitsuware.notepad;

import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.SealedObject;
import javax.crypto.Cipher;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Security;

public class StealthMessengerEncryption {
    
    private static final String ALGO = "SHA1";   
    
    static String algorithm = "PBEWITHSHA256AND128BITAES-CBC-BC";
    static char[] passPherase; 
    static byte[] salt = {'A', 'n', 'a', 'y', 'e', 'l', 'l', 'y'};
    static String secretData = "Very Secret Data!!";
    
public static String encrypt(String Data, String strEncKey) throws Exception {
					
		passPherase = strEncKey.toCharArray();
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt,10);
	    PBEKeySpec pbeKeySpec = new PBEKeySpec(passPherase, salt, 10, 8);
	    SecretKeyFactory secretKeyFactory = 
	    SecretKeyFactory.getInstance(algorithm);
	    SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
	    Cipher cipher = Cipher.getInstance(algorithm);
	    cipher.init(Cipher.ENCRYPT_MODE,secretKey,pbeParamSpec);	  			
        byte[] encVal = cipher.doFinal(Data.getBytes());
        String encryptedString = "";
        
        for(int i = 0; i < encVal.length;i++)
        {
        
        	if(i == encVal.length-1)
        		encryptedString += encVal[i];
        	else
        		encryptedString += encVal[i] + ";";
        }        
        
        return encryptedString;        
    }

    public static String decrypt(String encryptedData, String strEncKey) throws Exception {
    	passPherase = strEncKey.toCharArray();
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt,10);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(passPherase, salt, 10, 8);
        SecretKeyFactory secretKeyFactory = 
            SecretKeyFactory.getInstance(algorithm);
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE,secretKey,pbeParamSpec);
        String[] byteArr = encryptedData.split(";");
        byte[] encMsg = new byte[byteArr.length];
        for(int i=0; i < byteArr.length; i++)
        {
        	encMsg[i] = Byte.parseByte(byteArr[i]);
        } 
                
        byte[] decValue = cipher.doFinal(encMsg);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

}