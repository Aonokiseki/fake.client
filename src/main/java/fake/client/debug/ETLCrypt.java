package fake.client.debug;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class ETLCrypt {
	
	public final static String PASSWORD = "TrsDgEtl";
	public final static String CHARSET = "utf-8";
	
	public static void main(String[] args) {
		/* 将字符串s按照ETL的方式加密, 然后再解密 */
		String s = "trsadmin";
		try {
			String afterCrypt = globalCrypt(s);
			System.out.println(afterCrypt);
			String afterDecrypt = globalDecrypt(afterCrypt);
			System.out.println(afterDecrypt);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将明文加密为密文
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static String globalCrypt(String s) throws Exception{
		byte[] bytes = s.getBytes(Charset.forName(CHARSET));
		byte[] encrypted = desCrypto(bytes, PASSWORD, CHARSET);
		String base64 = base64Encode(encrypted);
		String urlencoded = URLEncoder.encode(base64, CHARSET);
		return urlencoded;
	}
	/**
	 * 将密文解密为明文
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static String globalDecrypt(String s) throws Exception{
		String urldecoded = URLDecoder.decode(s, CHARSET);
		byte[] unbase64 = base64Decode(urldecoded);
		byte[] decrypted = decrypt(unbase64, PASSWORD, CHARSET);
		return new String(decrypted);
	}
	
	private static byte[] desCrypto(byte[] datasource, String password, String charset) throws Exception {
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(password.getBytes(Charset.forName(charset)));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
		return cipher.doFinal(datasource);
	}
	
	private static byte[] decrypt(byte[] src, String password, String charset) throws Exception{
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new  DESKeySpec(password.getBytes(charset));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		return cipher.doFinal(src);
	}
	
	private static String base64Encode(byte[] bytes) {
		Base64.Encoder encoder = Base64.getEncoder();
		String result = encoder.encodeToString(bytes);
		return result;
	}
	
	private static byte[] base64Decode(String s) {
		Base64.Decoder decoder = Base64.getDecoder();
		return decoder.decode(s);
	}
}
