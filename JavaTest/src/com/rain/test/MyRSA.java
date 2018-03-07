package com.rain.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class MyRSA {

	public static void doRSATest() {
		String path1 = "resource/index.html";
		String text1 = loadFile(path1);
		
		String path2 = "resource/index2.html";
		String text2 = loadFile(path2);

		String algorithm = "MD5withRSA";

		 // testGenerateKeyPair(algorithm, text);

		if (testVerifyKeyPair(algorithm, text1)) {
			System.out.println("Verify OK : " + path1);
		} else {
			System.out.println("Verify Failed : " + path1);
		}
		
		if (testVerifyKeyPair(algorithm, text2)) {
			System.out.println("Verify OK : " + path2);
		} else {
			System.out.println("Verify Failed : " + path2);
		}
		
		// testRSA(text);
	}
	
	public static void testGenerateKeyPair(String algorithm, String text) {

		// 1. generate private&public key and save to file
		Map<String, byte[]> mapKeyPair = generateKeyPair();
		saveKeyData("output/private.dat", mapKeyPair.get("private"));
		saveKeyData("output/public.dat", mapKeyPair.get("public"));

		// 2. read private&public key form file
		byte[] privateKey = readKeyData("output/private.dat");

		// 3. signature source file
		byte[] signature = signatureFile(algorithm, privateKey, text);
		saveKeyData("output/signature.dat", signature);
	}
	
	public static boolean testVerifyKeyPair(String algorithm, String text) {

		byte[] publicKey = readKeyData("output/public.dat");
		byte[] signature = readKeyData("output/signature.dat");
		System.out.println("Signature : " + transferHexString(signature));
		//System.out.println("Signature2 : " + transferHexString(transferHexStringToByteArray(transferHexString(signature))));
		
		return verifySignature(algorithm, publicKey, text, signature);
	}
	
	private static String loadFile(String path) {
		StringBuilder builder = new StringBuilder();
		String encoding="UTF-8";
		
		File file = new File(path);
		if (file.isFile() && file.exists()) {
			InputStreamReader read = null;
			BufferedReader bufferedReader = null;
			try {
				read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					builder.append(lineTxt);
					builder.append("\n");
				}
			} catch (IOException e) {
				System.out.println("File read failed : " + path);
			}
			try {
				if (null != bufferedReader) {
					bufferedReader.close();
				}
				if (null != read) {
					read.close();
				}
			} catch (IOException e) {
				// skip
			}
		} else {
			System.out.println("File is not exist : " + path);
		}
		
		return builder.toString();
	}

	public static boolean saveKeyData(String path, byte[] value) {
		
		boolean result = false;
		
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		} else {
			
		}
		
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);

			dos.writeInt(value.length);
			dos.write(value);
			result = true;
		} catch (IOException e) {
			System.out.println("save key data failed : " + path);
			e.printStackTrace();
		} finally {
			if (null != dos) {
				try {
					fos.close();
				} catch (IOException e) {
					// skip
				}
			}
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					// skip
				}
			}
		}
		
		return result;
	}
	
	public static byte[] readKeyData(String path) {
		File file = new File(path);
		if (file.isFile() && file.exists()) {
			
		} else {
			System.out.println("File is not exist : " + path);
			return null;
		}
		
		byte[] value = null;
		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			
			//fos.write(b);
			int length = dis.readInt();
			value = new byte[length];
			fis.read(value);
		} catch (IOException e) {
			System.out.println("read key data failed : " + path);
			e.printStackTrace();
		} finally {
			if (null != dis) {
				try {
					fis.close();
				} catch (IOException e) {
					// skip
				}
			}
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					// skip
				}
			}
		}
		
		return value;
	}
	
	public static Map<String, byte[]> generateKeyPair() {
		
		Map<String, byte[]> mapKeyPair = new HashMap<String, byte[]>();
		
		// 1.初始化密钥
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
			
			mapKeyPair.put("private", rsaPrivateKey.getEncoded());
			mapKeyPair.put("public", rsaPublicKey.getEncoded());
		} catch (Exception e) {
			System.out.println("generate key pair failed");
			e.printStackTrace();
		}
		
		return mapKeyPair;
	}
	
	public static byte[] signatureFile(String algorithm, byte[] privateKeyData, String fileContents) {
		byte[] result = null;
		try {
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyData);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(privateKey);
			signature.update(fileContents.getBytes());
			result = signature.sign();
		} catch (Exception e) {
			System.out.println("signature file failed");
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static boolean verifySignature(String algorithm, byte[] publicKeyData, String fileContents, byte[] signatureData) {
		
		boolean reasult = false;
		
		try {
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyData);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
			Signature signature = Signature.getInstance(algorithm);
			signature.initVerify(publicKey);
			signature.update(fileContents.getBytes());
			reasult = signature.verify(signatureData);
		} catch (Exception e) {
			System.out.println("verify signature failed");
			e.printStackTrace();
		}
		
		return reasult;
	}

	public static void testRSA(String value) {
		try {
			// 1.初始化密钥
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

			// 2.执行签名
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(privateKey);
			signature.update(value.getBytes());
			byte[] result = signature.sign();
			// System.out.println("jdk rsa sign : " + Hex.encodeHexString(result));
	
			// 3.验证签名
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
			keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
			signature = Signature.getInstance("MD5withRSA");
			signature.initVerify(publicKey);
			signature.update(value.getBytes());
			boolean bool = signature.verify(result);
			System.out.println("jdk rsa verify : " + bool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String transferHexString(byte[] data) {
		StringBuilder builder = new StringBuilder();
		for (byte b : data) {
			builder.append(String.format("%X", b));
		}
		
		return builder.toString();
	}
	
	public static byte[] transferHexStringToByteArray(String value) {
		
		byte[] data = new byte[value.length()];

		for (int i=0; i<value.length(); i++) {
			char ch = value.charAt(i);
			data[i] = Byte.parseByte(String.valueOf(ch), 16);
		}
		
		return data;
	}
}
