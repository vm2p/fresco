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

import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.AbstractRoundBasedProtocol;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;

/**
 * Implements the {@link ExpSequenceProtocol} naively without the use of preprocessing.
 * <p>
 * We use the following method for a given non-zero <i>x</i>:
 * <ol>
 * <li> Denote by <i>i</i> the sequence of powers <i>x, ..., x<sup>i</sup></i> we have computed (initially <i>i = 1</i>)
 * <li> Compute the sequence <i>x<sup>i</sup> * x , ..., x<sup>i</sup> * x<sup>i</sup> = x<sup>i + 1</sup>, ..., x<sup>2i</sup></i>
 * <li> Repeat step 2 untill <i>i > k</i> 
 * </ol>
 * Note, that all multiplications in step 2 are independent, so each step can be done in a single round of multiplications.
 * Thus the entire protocol uses <i>k</i> multiplications performed in <i>log(k)</i> rounds.
 */
public class ExpSequenceProtocolImpl extends AbstractRoundBasedProtocol implements ExpSequenceProtocol {

	private SInt x;
	private SInt[] outputs;
	private int idx = 0;
	private BasicNumericFactory bnf;
		
	/**
	 * Constructs
	 * @param x
	 * @param outputs
	 * @param bnf
	 */
	public ExpSequenceProtocolImpl(SInt x, SInt[] outputs, BasicNumericFactory bnf) {
		this.x = x;
		this.outputs = outputs;
		this.bnf = bnf;
	}
	
	@Override
	public ProtocolProducer nextProtocolProducer() {
		NumericProtocolBuilder npb = new NumericProtocolBuilder(bnf);
		if (idx == 0) {
			npb.beginParScope();
			npb.copy(outputs[0], x);
			npb.mult(outputs[1], x, x);
			npb.endCurScope();	
			idx = 2;
		} else if (idx < outputs.length) {
			npb.beginParScope();
			for (int i = 0; i < idx && (idx + i) < outputs.length; i++) {
				npb.mult(outputs[idx + i], outputs[i], outputs[idx]);
			}
			idx = idx << 1;
			npb.endCurScope();
		} else {
			return null;
		}
		return npb.getProtocol();
	}

}
