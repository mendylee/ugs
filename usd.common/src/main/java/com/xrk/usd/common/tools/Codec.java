package com.xrk.usd.common.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Codec utils
 */
public class Codec {

    /**
     * @return an UUID String
     */
    public static String UUID() {    	
    	return UUID.randomUUID().toString().replace("-", "");
    }
    
    public static String RandomString()
    {
    	   return RandomString(36);
    } 
    
    public static String RandomString(int len)
    {
    	return RandomStringUtils.randomAlphanumeric(len);
    }

    /**
     * Encode a String to base64
     * @param value The plain String
     * @return The base64 encoded String
     * @throws UnsupportedEncodingException 
     */
    public static String encodeBASE64(String value) throws UnsupportedEncodingException {
        try {
            return new String(Base64.encodeBase64(value.getBytes("utf-8")));
        } catch (UnsupportedEncodingException ex) {
            throw ex;
        }
    }

    /**
     * Encode binary data to base64
     * @param value The binary data
     * @return The base64 encoded String
     */
    public static String encodeBASE64(byte[] value) {
        return new String(Base64.encodeBase64(value));
    }

    /**
     * Decode a base64 value
     * @param value The base64 encoded String
     * @return decoded binary data
     * @throws UnsupportedEncodingException 
     */
    public static byte[] decodeBASE64(String value) throws UnsupportedEncodingException {
        try {
            return Base64.decodeBase64(value.getBytes("utf-8"));
        } catch (UnsupportedEncodingException ex) {
        	throw ex;
        }
    }

    /**
     * Build an hexadecimal MD5 hash for a String
     * @param value The String to hash
     * @return An hexadecimal Hash
     * @throws Exception 
     */
    public static String hexMD5(String value) throws Exception {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(value.getBytes("utf-8"));
            byte[] digest = messageDigest.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
        	throw ex;
        }
    }

    /**
     * Build an hexadecimal SHA1 hash for a String
     * @param value The String to hash
     * @return An hexadecimal Hash
     * @throws Exception 
     */
    public static String hexSHA1(String value) throws Exception {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(value.getBytes("utf-8"));
            byte[] digest = md.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
        	throw ex;
        }
    }

    /**
     * Write a byte array as hexadecimal String.
     */
    public static String byteToHexString(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }

    /**
     * Transform an hexadecimal String to a byte array.
     * @throws DecoderException 
     */
    public static byte[] hexStringToByte(String hexString) throws DecoderException {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException ex) {
        	throw ex;
        }
    }

}
