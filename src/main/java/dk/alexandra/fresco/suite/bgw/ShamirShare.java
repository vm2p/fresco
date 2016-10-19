/*******************************************************************************
 * Copyright (c) 2015 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL,
 * and Bouncy Castle. Please see these projects for any further licensing issues.
 *******************************************************************************/
package dk.alexandra.fresco.suite.bgw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a share in Shamirs secret sharing scheme.
 * <p>
 * To share a secret <i>s</i> in <i>Z<sub>p</sub></i> between <i>n</i> parties
 * with a threshold of <i>t</i> the dealer will first generates a secret and
 * random polynomial <i>P</i> in <i>Z<sub>p</sub>[x]</i> of degree <i>t - 1</i>
 * so that <i>P(0) = s</i>. As a share each party receives the value of the
 * polynomial evaluated in a distinct point. We realize this, by having each
 * share be the pair of values <i>s<sub>i</sub> = (i, P(i))</i> with <i>i</i> in
 * <i>{1, ..., n}</i>, so that party <i>i</i> should receive <i>s<sub>i<sub></i>
 * . For a share <i>(x, y)</i> we call <i>x</i> the <i>point</i> and <i>y</i>
 * the <i>value</i> of the share.
 * </p>
 * 
 * TODO: check that this is 100% correct. In particular there may be one-off
 * error in the threshold.
 */
public final class ShamirShare implements Serializable {

	private static final long serialVersionUID = -7986019375218481628L;
	private static BigInteger[] vector;
	private static BigInteger primeNumber;
    private static SecureRandom random = new SecureRandom();
    private static int size;
    private static byte[] randomBytesBuffer;
    public static int partyId;
    private static int randomBytesMarker = 0;
    private byte point;
    private BigInteger fieldValue;

	/**
	 * Constructs a share given the evaluation point and the value of the secret
	 * polynomial at that point.
	 * 
	 * @param point
	 *            the point at which the polynomial is evaluated to generate
	 *            this share. The point must to be in the set {1, ..., 255}.
	 *            I.e., not more than 255 shares are supported.
	 * @param v
	 *            the value of the secret polynomial at the given point.
	 */
	public ShamirShare(int point, BigInteger v) {
		if (point > 255) {
			throw new IllegalArgumentException("Point is too large, it is more than 255.");
		}
		this.point = (byte) point;
		this.fieldValue = v.mod(primeNumber);
	}

	/**
	 * Constructs a share from a byte array. First byte is taken as the
	 * evaluation point, and the remaining bytes are taken to be the value of
	 * the polynomial at this point.
	 * 
	 * @param receivedData
	 *            a byte array representing a Shamir share.
	 */
	public ShamirShare(byte[] receivedData) {
		this.point = receivedData[0];
		int fieldSize = receivedData.length - 1;
		byte[] bytes = new byte[fieldSize];
		System.arraycopy(receivedData, 1, bytes, 0, fieldSize);
		this.fieldValue = new BigInteger(bytes);
	}
    
	/**
	 * Constructs a sharing of a known value
	 * 
	 * @param f
	 */
	public ShamirShare(BigInteger f) {
		this.fieldValue = f;
		this.point = -1;
	}

	/**
	 * Sets the modulus defining the field over which we are working.
	 * 
	 * @param mod
	 *            should be a prime.
	 */
	public static void setPrimeNumber(BigInteger mod) {
		primeNumber = mod;
	}

	/**
	 * Sets this share from a byte array. First byte is taken as the evaluation
	 * point, and the remaining bytes are taken to be the value of the
	 * polynomial at this point.
	 * 
	 * @param receivedData
	 *            a byte array representing a Shamir share.
	 */
	public void setBytes(byte[] receivedData) {
		this.point = receivedData[0];
		int fieldSize = receivedData.length - 1;
		byte[] bytes = new byte[fieldSize];
		System.arraycopy(receivedData, 1, bytes, 0, fieldSize);
		this.fieldValue = new BigInteger(bytes);
	}

	/**
	 * Get the value of the secret polynomial at the point associated with this
	 * share.
	 * 
	 * @return the value of the polynomial in this point
	 */
	public BigInteger getField() {
		return this.fieldValue;
	}
    
    private void writeObject(ObjectOutputStream out) throws IOException{
    	out.write(this.toByteArray());
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
    	byte[] b = new byte[ShamirShare.getSize()];
    	in.read(b);
    	ShamirShare s = new ShamirShare(b);
    	this.fieldValue = s.fieldValue;
    	this.point = s.point;
    }
    
	public void setField(BigInteger field) {
		this.fieldValue = field;
	}

	/**
	 * The point in which the secret polynomial is evaluated to generate this
	 * share.
	 * 
	 * @return the value of this point.
	 */
	public byte getPoint() {
		return this.point;
	}

	/**
	 * Sets the value of this point.
	 * 
	 * @param inx
	 *            the new value of this point.
	 */
	public void setPoint(int inx) {
		this.point = (byte) inx;
	}

	/**
	 * TODO: What is this!?
	 * 
	 * @return
	 */
	public static int getSize() {
		return ShamirShare.size + 1;
	}

	/**
	 * Generate a byte array representation of this share.
	 * 
	 * @return a byte array
	 */
	public byte[] toByteArray() {
		byte[] bytes = new byte[ShamirShare.getSize()];
		bytes[0] = this.point;
		this.copyAndInvertArray(bytes, this.fieldValue.toByteArray());
		return bytes;
	}

	private void copyAndInvertArray(byte[] bytes, byte[] byteArray) {
		for (int inx = 0; inx < byteArray.length; inx++) {
			bytes[bytes.length - byteArray.length + inx] = byteArray[inx];
		}
	}

	@Override
	public String toString() {
		return "(" + this.point + ", " + this.fieldValue.toString() + ")";
	}

	/**
	 * Multiplies the value of this share with the value of an other share.
	 * Note, this generates an incorrect share.
	 * 
	 * @param other
	 *            an other Shamir share.
	 * @return the resulting share.
	 */
	public ShamirShare mult(ShamirShare other) {
		return new ShamirShare(this.point, this.fieldValue.multiply(other.fieldValue));
	}

	/**
	 * Reconstructs a secret from a set of shares given a number of parties
	 * TODO: What is the number of parties supposed to mean?
	 * 
	 * @param shares
	 *            the share to reconstruct the secret from
	 * @param numberOfParties
	 *            ?
	 * @return the reconstructed secret
	 */
	public static BigInteger recombine(ShamirShare[] shares, int numberOfParties) {
		if ((vector != null) && (vector.length == numberOfParties)) {
			return recombine(shares);
		} else {
			vector = computeCoefficients(numberOfParties);
			return recombine(shares);
		}
	}

	/**
	 * Reconstructs a secret from a set of shares given a number of parties
	 * TODO: What is the number of parties supposed to mean?
	 * 
	 * @param shares
	 *            the share to reconstruct the secret from
	 * @param numberOfParties
	 *            ?
	 * @return the reconstructed secret
	 */
	public static BigInteger recombine(List<ShamirShare> shares, int numberOfParties) {
		ShamirShare[] tmp = shares.toArray(new ShamirShare[shares.size()]);
		return recombine(tmp, numberOfParties);
	}

	private static BigInteger recombine(ShamirShare[] shares) {
		BigInteger s = BigInteger.ZERO;
		for (int inx = 0; inx < vector.length; inx++) {
			ShamirShare share = shares[inx];
			s = s.add(share.fieldValue.multiply(vector[inx]).mod(primeNumber)).mod(primeNumber);
		}
		return s.mod(primeNumber);
	}

	/**
	 * Computes coefficients used for reconstruction. These can be computed
	 * independently from the actual shares.
	 * 
	 * @param numberOfParties
	 *            ??
	 * @return an array of coefficients
	 */
	private static BigInteger[] computeCoefficients(int numberOfParties) {
		BigInteger[] vector = new BigInteger[numberOfParties];
		for (byte pi = 1; pi <= numberOfParties; pi++) {
			List<BigInteger> factors = new ArrayList<BigInteger>(numberOfParties);
			for (byte pk = 1; pk <= numberOfParties; pk++) {
				if (pi != pk) {
					BigInteger x_k = BigInteger.valueOf(pk);
					BigInteger x_i = BigInteger.valueOf(pi);
					BigInteger subtractionResult = x_k.subtract(x_i);
					BigInteger subtractionResultModInverse = subtractionResult.modInverse(primeNumber);
					BigInteger multiplicationResult = x_k.multiply(subtractionResultModInverse);
					factors.add(multiplicationResult.mod(primeNumber));
				}
			}
			if (factors.size() > 0) {
				BigInteger r = factors.remove(0);
				for (BigInteger f : factors) {
					r = r.multiply(f).mod(primeNumber);
				}
				vector[pi - 1] = r.mod(primeNumber);
			}
		}
		return vector;
	}

	/**
	 * * Creates a sharing of a given secret
	 * 
	 * @param secret
	 *            the secret to share
	 * @param numberOfParties
	 *            the number parties who should receive a share
	 * @param threshold
	 *            the threshold of the sharing
	 * @return an array of shares of the secret
	 */
	public static ShamirShare[] createShares(BigInteger secret, int numberOfParties, int threshold) {
		List<BigInteger> coefficients = new ArrayList<BigInteger>(threshold);
		coefficients.add(secret);
		for (int inx = 0; inx < threshold; inx++) {
			coefficients.add(ShamirShare.random());
		}
		ShamirShare[] shares = new ShamirShare[numberOfParties];
		for (int inx = 1; inx <= numberOfParties; inx++) {
			// Instead of calculating s_i as
			// s_i = s + a_1 x_i + a_2 x_i^2 + ... + a_t x_i^t
			//
			// we avoid the exponentiations by calculating s_i by
			//
			// s_i = s + x_i (a_1 + x_i (a_2 + x_i ( ... (a_t) ...
			// )))
			//
			// This is a little faster, even for small n and t.
			BigInteger cur_point = BigInteger.valueOf(inx);
			BigInteger cur_share = coefficients.get(threshold);
			// Go backwards from this.threshold-1 down to 0
			for (int inj = threshold - 1; inj >= 0; inj--) {
				cur_share = coefficients.get(inj).add(cur_share.multiply(cur_point));
			}
			shares[inx - 1] = new ShamirShare(inx, cur_share);
		}
		return shares;
	}

	/**
	 * Generates a random BigInteger from 8 random bytes
	 * 
	 * TODO: fix this! This should be a random BigInteger in the prime field!
	 * 
	 * @return the generated BigInteger
	 */
	public static BigInteger random() {
		byte[] bytes = new byte[8];
		if ((randomBytesBuffer != null) && (randomBytesMarker + 8 < randomBytesBuffer.length)) {
			System.arraycopy(randomBytesBuffer, randomBytesMarker, bytes, 0, 8);
			randomBytesMarker += 8;
			return new BigInteger(bytes);
		} else {
			randomBytesBuffer = random0(16384);
			randomBytesMarker = 0;
			System.arraycopy(randomBytesBuffer, randomBytesMarker, bytes, 0, 8);
			randomBytesMarker += 8;
		}
		return new BigInteger(bytes);
	}

	/**
	 * Get an array of random bytes
	 * 
	 * @param numberOfBytes
	 *            the number of bytes
	 * @return an array of random bytes with the given length
	 */
	private static byte[] random0(int numberOfBytes) {
		byte bytes[] = new byte[numberOfBytes];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * Set the seed of the random number generator used to generate shares.
	 * 
	 * @param seed
	 */
	public static void setRandomSeed(byte[] seed) {
		random = new SecureRandom(seed);
	}

	/**
	 * Deserialises a byte array into a shamir share array. NOT TESTED YET
	 * 
	 * @param bytes
	 *            Array of bytes
	 * @return Array of shamir shares
	 */
	public static ShamirShare[] deSerializeArray(byte[] bytes, int count) {
		ShamirShare[] res = new ShamirShare[count];
		int counter = 0;
		int indx = 0;
		byte[] b = null;
		while (indx < bytes.length) {
			byte l = bytes[indx];
			b = new byte[l];
			System.arraycopy(bytes, indx, b, 0, l);
			res[counter] = new ShamirShare(b);
			counter++;
			indx = indx + l;
		}
		return res;
	}

	/**
	 * Deserialize a bytes array into a single share.
	 * 
	 * @param bytes
	 *            the bytes
	 * @param offset
	 *            an offset
	 * @return the resulting share
	 */
	public static ShamirShare deSerialize(byte[] bytes, int offset) {
		byte[] b = new byte[getSize()];
		System.arraycopy(bytes, offset, b, 0, getSize());
		return new ShamirShare(b);
	}
}
