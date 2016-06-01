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
package dk.alexandra.fresco.lib.newcompare.subprotocols;

import java.util.Arrays;

import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.MiscOIntGenerators;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestBruteforceProtocol;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.AbstractRoundBasedProtocol;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFactory;
import dk.alexandra.fresco.lib.math.integer.linalg.InnerProductFactory;
import dk.alexandra.fresco.lib.math.integer.linalg.InnerProductFactoryImpl;

/**
 * A protocol testing if value <i>x</i> in the interval <i>[0, ..., k]</i> is
 * zero, for some given (small) <i>k</i>.
 * <p>
 * We use the following test:
 * <ol>
 * <li>Compute <i>x' = x + 1</i> so that <i>x'</i> is certainly not zero (this
 * is required for the method used in the next step)
 * <li>Compute the sequence <i>x', x' <sup>2</sup>, ..., x' <sup>k</sup></i>
 * <li>Compute <b>open</b> coefficients <i>c<sub>0</sub>, ..., c<sub>k</sub></i>
 * of the polynomial <i>p</i>, so that <i>p(x') = 1</i> iff <i>x' = 1</i> and
 * <i>p(x') = 0</i> otherwise
 * <li>Evaluate the polynomial <i>p</i> by computing the inner product between
 * the vectors <i>(x', x' <sup>2</sup>, ..., x' <sup>k</sup>)</i> and <i>(c
 * <sub>1</sub>, ..., c<sub>k</sub>)</i> and adding <i>c<sub>0<sub></i>
 * <li>The result of the polynomial evaluation equals the result of the zero
 * test.
 * </ol>
 * 
 * Note that all this can be done locally except for (perhaps) the 2nd step. The 
 * complexity of the 2nd step is dependent on the concrete subprotocol used.  
 *  
 */
public class PolynomialZeroTest extends AbstractRoundBasedProtocol implements ZeroTestBruteforceProtocol {

	enum State {
		COMP_POWERS, EVAL_POLY, DONE
	}

	private ExpFactory expFac;
	private BasicNumericFactory bnFac;
	private MiscOIntGenerators miscGen;
	private State state = State.COMP_POWERS;
	private int max;
	private SInt x, result;
	private SInt[] powers;
		
	/**
	 * @param x input - the value to zero test (should be reduced to be rather small and non-negative)
	 * @param result output - the result of the zero test
	 * @param max an assumed about on the value of <code>x</code>
	 * @param miscGen a generator for various OInt's
	 * @param expFac a factory providing computation of exponential sequences
	 * @param bnFac a factory providing basic numeric functionality
	 */
	public PolynomialZeroTest(SInt x, SInt result, int max, MiscOIntGenerators miscGen, ExpFactory expFac,
			BasicNumericFactory bnFac) {
		super();
		this.x = x;
		this.result = result;
		this.max = max;
		this.miscGen = miscGen;
		this.expFac = expFac;
		this.bnFac = bnFac;
	}


	@Override
	public ProtocolProducer nextProtocolProducer() {
		NumericProtocolBuilder npb = new NumericProtocolBuilder(bnFac);
		switch (state) {
		case COMP_POWERS:
			powers = npb.getSIntArray(max);
			SInt one = npb.known(1);
			SInt xPrime = npb.add(one, x);
			npb.addProtocolProducer(expFac.getExpSequenceProtocol(xPrime, powers));
			state = State.EVAL_POLY;
			break;
		case EVAL_POLY:
			OInt[] coeffs = Arrays.copyOfRange(miscGen.getPoly(powers.length), 1, powers.length + 1);
			InnerProductFactory ipf = new InnerProductFactoryImpl(bnFac);
			npb.addProtocolProducer(ipf.getInnerProductProtocol(powers, coeffs, result));
			npb.add(result, result, coeffs[0]);
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
