/**
 * This file is part of ${project_name}.
 * 
 * ${project_name} is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ${project_name} is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ${project_name}. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @see http://www.gnu.org/licenses/lgpl.txt
 * @author art <runtimestate@gmail.com>
 */
package com.life.android.pwmanager.dao;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHelper {

	private final static String algorithm = "AES";

	public static String encrypt(String content, String... passwords) {
		if (passwords.length > 1) {
			return "";
		}
		if (passwords.length == 0) {
			return getMD5(content);
		}
		String password = passwords[0];
		byte[] encryptResult = crypt(content, password, Cipher.ENCRYPT_MODE);
		return parseByte2HexStr(encryptResult);
	}

	private static String getMD5(String content) {
		String password = "";
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			byte[] b = content.getBytes("UTF8");
			byte[] hash = messageDigest.digest(b);
			password = parseByte2HexStr(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return password;
	}

	public static String decrypt(String content, String password) {
		byte[] decryptResult = crypt(content, password, Cipher.DECRYPT_MODE);
		return new String(decryptResult);
	}

	private static byte[] crypt(String content, String password, int mode) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, algorithm);
			Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
			byte[] byteContent = new byte[] {};
			if (Cipher.ENCRYPT_MODE == mode) {
				byteContent = content.getBytes("utf-8");
			}
			if (Cipher.DECRYPT_MODE == mode) {
				byteContent = parseHexStr2Byte(content);
			}
			cipher.init(mode, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public void test() {
		String content = "test";
		String password = "12345678";
		// 加密
		System.out.println("加密前：" + content);
		String encryptResultStr = encrypt(content, password);
		System.out.println("加密后：" + encryptResultStr);
		// 解密
		String decryptResultStr = decrypt(encryptResultStr, password);
		System.out.println("解密后：" + decryptResultStr);
	}
}
