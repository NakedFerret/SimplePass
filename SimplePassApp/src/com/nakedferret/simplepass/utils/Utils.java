package com.nakedferret.simplepass.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.util.Log;

public class Utils {

	public static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
	public static final String KEY_SPEC = "AES";

	public static final int SALT_SIZE = 32;
	public static final Digest DIGEST = new SHA256Digest();

	public static byte[] getSalt() {
		byte[] salt = new byte[SALT_SIZE];
		new SecureRandom().nextBytes(salt);
		return salt;
	}

	public static byte[] getKey(String pass, int iterations) {
		byte[] salt = new byte[SALT_SIZE];
		new SecureRandom().nextBytes(salt);
		return getKey(pass, salt, iterations);
	}

	public static byte[] getKey(String pass, byte[] salt, int iterations) {
		char[] passChar = pass.toCharArray();
		byte[] passBytes = PBEParametersGenerator
				.PKCS5PasswordToUTF8Bytes(passChar);
		PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(DIGEST);
		gen.init(passBytes, salt, iterations);
		KeyParameter key = (KeyParameter) gen.generateDerivedMacParameters(256);
		return key.getKey();
	}

	public static byte[] getHash(byte[] encPass, byte[] salt) {
		Digest d = DIGEST;
		d.reset();

		d.update(encPass, 0, encPass.length);
		d.update(salt, 0, salt.length);

		byte[] hash = new byte[d.getDigestSize()];
		d.doFinal(hash, 0);
		return hash;
	}

	public static void log(Object o, String message) {
		message = o.getClass().getSimpleName() + " - " + message;
		Log.d("SimplePass", message);
	}

	public static Cipher getEncryptionCipher(byte[] keyValue) {
		Key key = new SecretKeySpec(keyValue, KEY_SPEC);
		try {
			Cipher c = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Cipher getEncryptionCipher(byte[] keyValue, byte[] iv) {
		Key key = new SecretKeySpec(keyValue, KEY_SPEC);
		try {
			Cipher c = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Cipher getDecryptionCipher(byte[] keyValue, byte[] iv) {
		Key key = new SecretKeySpec(keyValue, KEY_SPEC);
		try {
			Cipher c = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] encrypt(Cipher c, String string) {
		try {
			return c.doFinal(string.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] decrypt(Cipher c, byte[] data) {
		try {
			return c.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isServiceRunning(Context c,
			Class<? extends Service> serviceClass) {
		ActivityManager manager = (ActivityManager) c
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static Object proxyListener(final Object listener, Class[] interfaces) {
		return Proxy.newProxyInstance(Utils.class.getClassLoader(), interfaces,
				new InvocationHandler() {
					public Object invoke(Object proxy, Method method,
							Object[] args) {
						try {
							return method.invoke(listener, args);
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}
				});
	}
}
