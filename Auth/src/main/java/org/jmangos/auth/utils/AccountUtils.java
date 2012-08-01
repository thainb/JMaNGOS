/*******************************************************************************
 * Copyright (C) 2012 JMaNGOS <http://jmangos.org/>
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.jmangos.auth.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.jmangos.auth.model.Account;
import org.jmangos.commons.network.model.NettyNetworkChannel;
import org.jmangos.commons.utils.BigNumber;

// TODO: Auto-generated Javadoc
/**
 * The Class AccountUtils.
 */
public class AccountUtils {
	
	/** The Constant N. */
	public static final BigNumber N = new BigNumber("894B645E89E1535BBDAD5B8B290650530801B18EBFBF5E8FAB3C82872A3E9BB7");
	
	/** The Constant g. */
	public static final BigNumber g = new BigNumber("7");
	
	/** The Constant k. */
	public static final BigNumber k = new BigNumber("3");

	/**
	 * Sets the vs fields.
	 *
	 * @param Ir the ir
	 * @return the hash map
	 */
	public static HashMap<String, BigNumber> setVSFields(String Ir)
	{
		BigNumber I = new BigNumber(Ir);
		HashMap<String, BigNumber> res = new HashMap<String, BigNumber>();
		byte[] hash = I.asByteArray(20);
		
		int length = hash.length;
		for (int i = 0; i < length / 2; i++)
		{
			byte j = hash[i];
			hash[i] = hash[length - 1 - i];
			hash[length - 1 - i] = j;
		}
		System.out.println("passhash:"+new BigInteger(1, hash).toString(16).toUpperCase());
		BigNumber s = new BigNumber();
		s.setRand(32);
		System.out.println("s: " + s.asHexStr());
		
		MessageDigest sha = null;
		try
		{
			sha = MessageDigest.getInstance("SHA-1");
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
		sha.update(s.asByteArray(32));
		sha.update(hash);
		BigNumber x = new BigNumber();
		x.setBinary(sha.digest());
		System.out.println("x: " + x.asHexStr());
		BigNumber verifier = g.modPow(x, N);
		System.out.println("v: " + verifier.asHexStr());
		res.put("v", verifier);
		res.put("s", s);
		return res;
	}
	 
	/**
	 * Gets the b.
	 *
	 * @param v the v
	 * @param con the con
	 * @return the b
	 */
	public static BigNumber getB(BigNumber v, NettyNetworkChannel con){
		
		BigNumber b = new BigNumber(); 
		b.setRand(19);
		((Account)con.getChanneledObject()).setB(b);
		BigNumber gmod = g.modPow(b, N);
		return ((v.multiply(k)).add(gmod)).mod(N);
		
	}
}