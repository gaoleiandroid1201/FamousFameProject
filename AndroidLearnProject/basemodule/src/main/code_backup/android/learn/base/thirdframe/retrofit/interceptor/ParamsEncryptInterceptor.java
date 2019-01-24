package com.android.learn.base.thirdframe.retrofit.interceptor;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class ParamsEncryptInterceptor implements Interceptor {

  private static final String TAG = ParamsEncryptInterceptor.class.getSimpleName();

  private static final boolean DEBUG = true;

   @Override
   public Response intercept(Chain chain) throws IOException {


   Request request = chain.request();
    try {
     RequestBody oldBody = request.body();
     Buffer buffer = new Buffer();
     oldBody.writeTo(buffer);
     String strOldBody = buffer.readUtf8();
     MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
     //在发动报文前，用Base64和AES对参数进行加密转码
     String strNewBody = encryptDataWithSymmetricKey(generateSymmetric(), strOldBody);
     RequestBody body = RequestBody.create(mediaType, strNewBody);
     request = request.newBuilder().header("Content-Type", body.contentType().toString())
             .header("Content-Length", String.valueOf(body.contentLength())).method(request.method(), body).build();
    } catch (NoSuchAlgorithmException e) {
     e.printStackTrace();
    } catch (InvalidKeySpecException e) {
     e.printStackTrace();
    }
    return chain.proceed(request);
}

 //加密算法自己和服务端约定即可
 private static String encrypt(String str){
  //your code
  return "";
 }

 //生成了对称密钥
 public static SecretKeySpec generateSymmetric() {
  // Set up secret key spec for 128-bit AES encryption and decryption
  SecretKeySpec sks = null;
  try {
   SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
   sr.setSeed("any data used as random seed".getBytes());
   KeyGenerator kg = KeyGenerator.getInstance("AES");
   kg.init(128, sr);
   SecretKey secretKey=kg.generateKey();
   //在这里可以用从服务端获取到的RSA公钥对刚才随机产生的加密密钥secretKey进行加密
   sks = new SecretKeySpec(secretKey.getEncoded(), "AES");

   System.out.println("AES KEY: " + sks);
  } catch (Exception e) {
   Log.e(TAG, "AES secret key spec error");
  }
  return sks;
 }

//用SecretKeySpec加密数据
 private static String encryptDataWithSymmetricKey (SecretKeySpec symmKey, String data) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

  // encryption
  byte[] toBeCiphred = data.getBytes("UTF-8");
  String encryptedData = null;

  try {
   Cipher c = Cipher.getInstance("AES");
   c.init(Cipher.ENCRYPT_MODE, symmKey);
   byte[] encodedBytes = c.doFinal(toBeCiphred);
   System.out.println("BYTE STRING (ASYMM): " + encodedBytes);
   //将SecretKeySpec转换为Base64字符串格式
   encryptedData = Base64.encodeToString(encodedBytes, Base64.DEFAULT);

  } catch (Exception e) {
   Log.e(TAG, "AES encryption error");
   throw new RuntimeException(e);
  }
  return encryptedData;
 }
}