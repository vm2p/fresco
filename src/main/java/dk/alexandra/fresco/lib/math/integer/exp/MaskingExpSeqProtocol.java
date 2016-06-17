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
package dk.alexandra.fresco.lib.math.integer.exp;

import java.math.BigInteger;

import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.AbstractRoundBasedProtocol;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;

/**
 * Implements an exponentiation sequence generating protocol using a random
 * exponentiation sequence.
 * <p>
 * We use the following method for a given non-zero input <i>x</i>:
 * <ol>
 * <li>Generate the closed sequence <i>r <sup>-1</sup>, r, r<sup>2</sup>, ... r
 * <sup>k</sup></i> for a random <i>r</i> (alternatively such a sequence can be
 * given directly in the constructor)
 * <li> Compute and open the value <i>m = r <sup>-1</sup>*x</i>
 * <li> Compute the open sequence <i>m, m<sup>2</sup>, ... ,m<sup>k</sup></i> in the clear
 * <li> Using the closed sequence generated in the first step compute the closed sequence  
 * <i>m*r, m<sup>2</sup>*r<sup>2</sup>, ... ,m<sup>k</sup>r<sup>k</sup> = x, x<sup>2</sup>, ... ,x<sup>k</sup></i> 
 * </ol>
 * Given step 1 (which can be preprocessed), the remainder of the protocol requires only
 * 1 closed multiplication.  
 */
public class MaskingExpSeqProtocol extends AbstractRoundBasedProtocol implements ExpSequenceProtocol {
	private enum State {
		LOAD_RAND, MASK, UNMASK, DONE
	}

	private State state = State.LOAD_RAND;
	private ExpFactory expFac;
	private BasicNumericFactory bnFac;
	private ExpFromOIntFactory oExpFac;
	private SInt x;
	private SInt[] powers, randPowers;
	private OInt m;

	/**
	 * Constructs a MaskingExpSeqProtocol using an {@link ExpFactory} to
	 * generate the random exponentiation sequence.
	 * 
	 * @param x
	 *            input - the base of the exponentiation sequence
	 * @param powers
	 *            output - SInts to hold the exponentiation sequence
	 * @param bnFac
	 *            a factory for basic numeric protocols
	 * @param expFac
	 *            a factory for random exponentiation sequences
	 */
	public MaskingExpSeqProtocol(SInt x, SInt[] powers, BasicNumericFactory bnFac, ExpFactory expFac, ExpFromOIntFactory oExpFac) {
		this.x = x;
		this.powers = powers;
		this.bnFac = bnFac;
		this.expFac = expFac;
		this.oExpFac = oExpFac;
		this.state = State.LOAD_RAND;
	}

	/**
	 * Constructs a MaskingExpSeqProtocol using a given random exponentiation
	 * sequence.
	 * 
	 * @param x
	 *            input - the base of the exponentiation sequence
	 * @param randPowers
	 *            input - a random exponentiation sequence (<i>r<sup>-1</sup>,
	 *            r, r<sup>2</sup>,...,r<sup>k</sup></i>)
	 * @param powers
	 *            output - SInts to hold the exponentiation sequence (x, x
	 *            <sup>2</sup>,...,x<sup>k</sup></i>)
	 * @param bnFac
	 *            a factory for basic numeric protocols
	 * @param expFac
	 *            a factory for random exponentiation sequences
	 */

	public MaskingExpSeqProtocol(SInt x, SInt[] randPowers, SInt[] powers, BasicNumericFactory bnFac, ExpFromOIntFactory oExpFac) {
		this.x = x;
		this.powers = powers;
		this.randPowers = randPowers;
		this.bnFac = bnFac;
		this.oExpFac = oExpFac;
		this.state = State.MASK;
		if (powers.length + 1 != randPowers.length) {
			throw new IllegalArgumentException("Lengths does not match. The random exponentiation sequence "
					+ "must be as one element longer than the output sequence.");
		}
	}

	@Override
	public ProtocolProducer nextProtocolProducer() {
		NumericProtocolBuilder npb = new NumericProtocolBuilder(bnFac);
		switch (state) {
		case LOAD_RAND:
			randPowers = npb.getSIntArray(powers.length + 1);
			npb.addProtocolProducer(expFac.getExponentiationProtocol(randPowers));
			state = State.MASK;
			break;
		case MASK:
			m = bnFac.getOInt();
			SInt tmp = npb.mult(x, randPowers[0]);
			npb.addProtocolProducer(bnFac.getOpenProtocol(tmp, m));
			state = State.UNMASK;
			break;
		case UNMASK:
			BigInteger value = m.getValue();
			npb.beginParScope();
			OInt oval = bnFac.getOInt(value);
			OInt[] oPowers = oExpFac.getExpFromOInt(oval, powers.length);
			npb.copy(powers[0], x);
			for (int i = 1; i < powers.length; i++) {
				npb.addProtocolProducer(bnFac.getMultProtocol(oPowers[i], randPowers[i + 1], powers[i]));
			}			
			npb.endCurScope();
			state = State.DONE;
			break;
		case DONE:
			return null;
		default:
			break;
		}
		return npb.getProtocol();
	}

}
