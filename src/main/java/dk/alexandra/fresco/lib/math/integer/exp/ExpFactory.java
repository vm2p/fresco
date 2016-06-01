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

import dk.alexandra.fresco.framework.value.SInt;

public interface ExpFactory {

	/**
	 * A protocol that generates a sequence of values <i>r<sup>-1</sup>, r, r<sup>2</sup>
	 * , ..., r<sup>k</sup></i> for a random <i>r</i>. 
	 * 
	 * @param pipe output - an array in which to put the generated sequence
	 * @return the protocol
	 */
	public ExponentiationPipeProtocol getExponentiationProtocol(SInt[] pipe);
	
	/**
	 * A protocol that generates a sequence <i>x, x<sup>2</sup>
	 * , ..., x<sup>k</sup></i> for a given non-zero <i>x</i>.
	 * 
	 * Note, for x = 0 this may not be secure!
	 * 
	 * @param x input - the base value of the exponentiation sequence
	 * @param powers output - an array in which to put the generated sequence
	 * @return the protocol
	 */
	public ExpSequenceProtocol getExpSequenceProtocol(SInt x, SInt[] powers);
}
