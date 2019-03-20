package com.salamander.salamander_network.utils;

/**
 * Created by benny_aziz on 09/05/2017.
 */

public class Hash {
    /**
     * @param txt,     text in plain format
     * @param hashType MD5 OR SHA1
     * @return hash in hashType
     */
    public static String getHash(String txt, HASH_TYPE hashType) {
        try {
            java.security.MessageDigest md = null;
            if (hashType == HASH_TYPE.MD5)
                md = java.security.MessageDigest.getInstance("MD5");
            else if  (hashType == HASH_TYPE.SHA1)
                md = java.security.MessageDigest.getInstance("SHA1");

            if (md == null)
                return null;

            byte[] array = md.digest(txt.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String md5(String strToHash) {
        return Hash.getHash(strToHash, HASH_TYPE.MD5);
    }

    public static String sha1(String strToHash) {
        return Hash.getHash(strToHash, HASH_TYPE.SHA1);
    }

    public enum HASH_TYPE {
        MD5,
        SHA1
    }
}
