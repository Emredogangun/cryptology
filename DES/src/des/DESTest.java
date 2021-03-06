/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package des;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;



public class DESTest {
    Cipher eCipher;
    Cipher dCipher;
    SecretKey generatedKey;
    SecretKey readedKey;

    public DESTest() {
    }

    public boolean generateKey() {
        try {
            generatedKey = KeyGenerator.getInstance("DES").generateKey();
            eCipher = Cipher.getInstance("DES");
            dCipher = Cipher.getInstance("DES");
            eCipher.init(Cipher.ENCRYPT_MODE, generatedKey);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Böyle bir Algoritma İstisnası Yok: " + e.getMessage());
            return false;
        } catch (javax.crypto.NoSuchPaddingException e) {
            System.err.println("Böyle bir dolgu istisnası yok: " + e.getMessage());
            return false;
        } catch (java.security.InvalidKeyException e) {
            System.err.println("Geçersiz Anahtar İstisnası:" + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean saveKey() {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            Class spec = Class.forName("javax.crypto.spec.DESKeySpec");
            DESKeySpec ks = (DESKeySpec)skf.getKeySpec(generatedKey, spec);
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(".key"));
            oos.writeObject(ks.getKey());
            oos.close();
        } catch (Exception e) {
            System.err.println("Save Key: " + e.getMessage());
        }
        return true;
    }

    public boolean readKey() {
        try {
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(".key"));
            DESKeySpec ks = new DESKeySpec((byte[])ois.readObject());
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            readedKey = skf.generateSecret(ks);
            dCipher.init(Cipher.DECRYPT_MODE, readedKey);
        } catch (Exception e) {
            System.err.println("Read Key: " + e.getMessage());
        }
        return true;
    }

    public String encode(String str) {
        byte[] utf8;
        try {
            utf8 = str.getBytes("UTF8");
            byte[] enc = eCipher.doFinal(utf8);

            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (UnsupportedEncodingException e) {
            System.err.println("Desteklenmeyen Kodlama İstisnası: " +
                               e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.err.println("Geçersiz Blok Boyutu İstisnası: " + e.getMessage());
        } catch (BadPaddingException e) {
            System.err.println("Kötü Dolgu İstisnası: " + e.getMessage());
        }
        return null;
    }

    public String decode(String str) {
        try {
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

            byte[] utf8 = dCipher.doFinal(dec);

            return new String(utf8, "UTF8");
        } catch (javax.crypto.BadPaddingException e) {
            System.err.println("Kötü Dolgu İstisnası: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.err.println("Geçersiz Blok Boyutu İstisnası:" + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.err.println("Desteklenmeyen Kodlama İstisnası: " +
                               e.getMessage());
        } catch (java.io.IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return null;
    }
}