package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Helper {

	// Class will contain helper methods only
	
	public static String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		byte[] hashBytes = digest.digest(data);
		BigInteger hashInt = new BigInteger(1, hashBytes);
		String hash = hashInt.toString(16);
		while(hash.getBytes().length<40) {
			String temp = "0"+hash;
			hash = temp;
		}
		return hash;
	}
	
}
