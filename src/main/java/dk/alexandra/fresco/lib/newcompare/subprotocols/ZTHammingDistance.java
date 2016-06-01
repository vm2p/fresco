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

import java.math.BigInteger;

import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.AbstractRoundBasedProtocol;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.lib.math.integer.HammingDistanceProtocol;

/**
 * A protocol to compute the Hamming distance between an open integer <i>b</i> and a
 * closed integer <i>a</i> represented as an array of bits <i>a<sub>1</sub>, ..., a<sub>k</sub></i> (i.e., 1/0 values). 
 * <p>
 * We use the follow method:
 * <ul>
 * <li> Let <i>b<sub>1</sub>, ..., b<sub>k</sub></i> be the bit representation of b. This can be found locally as b is open
 * <li> Compute the bitwise XOR <i>x<sub>1</sub>, ..., x<sub>k</sub></i> where <i>x<sub>i</sub> = a<sub>i</sub> XOR b<sub>i</sub></i>. This can be computed as <i>x<sub>i</sub> = a<sub>i</sub></i> if <i>b<sub>i</sub> = 0</i> and <i>x<sub>i</sub> = 1 - a<sub>i</sub></i> otherwise
 * <li> Compute the sum of the values <i>x<sub>1</sub>, ..., x<sub>k</sub></i> to get the hamming distance
 * </ul>
 * Note that all operations can be done locally.
 */
public class ZTHammingDistance extends AbstractRoundBasedProtocol implements HammingDistanceProtocol {

	private enum State {
		XOR, SUM, DONE
	}
	private SInt[] as, xs;
	private OInt b;
	private SInt h, one;
	private BasicNumericFactory bnf;
	private State state = State.XOR;

	/**
	 * Constructs a Hamming distance protocol
	 * 
	 * @param as
	 *            input - a bit representation of an integer (Note: the
	 *            behaviour is not well understood if these values are not
	 *            either 0 or 1)
	 * @param b
	 *            input - an open integer
	 * @param h
	 *            output - a closed integer holding the Hamming distance between
	 *            the two integers bit representations
	 * @param bnf
	 *            a factory for basic numeric operations
	 */
	public ZTHammingDistance(SInt[] as, OInt b, SInt h, BasicNumericFactory bnf) {
		this.as = as;
		this.b = b;
		this.h = h;
		this.bnf = bnf;
	}

	@Override
	public ProtocolProducer nextProtocolProducer() {
		BigInteger m = b.getValue();
		NumericProtocolBuilder npb = new NumericProtocolBuilder(bnf);
		switch (state) {
		case XOR: // Compute the bitwise XOR between a and b 
			one = npb.known(1);
			xs = new SInt[as.length];
			npb.beginParScope();
			for (int i = 0; i < as.length; i++) {
				if (m.testBit(i)) {
					xs[i] = npb.sub(one, as[i]);
				} else {
					xs[i] = npb.getSInt();
					npb.copy(xs[i], as[i]);
				}
			}
			npb.endCurScope();
			state = State.SUM;
		case SUM: // Sum the XOR bits
			SInt sum = npb.sum(xs);
			npb.copy(h, sum);
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
