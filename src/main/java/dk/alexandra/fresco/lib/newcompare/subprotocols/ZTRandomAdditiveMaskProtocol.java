package dk.alexandra.fresco.lib.newcompare.subprotocols;

import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.MiscOIntGenerators;
import dk.alexandra.fresco.lib.compare.RandomAdditiveMaskProtocol;
import dk.alexandra.fresco.lib.helper.AbstractRoundBasedProtocol;
import dk.alexandra.fresco.lib.math.integer.linalg.InnerProductFactory;

public class ZTRandomAdditiveMaskProtocol extends AbstractRoundBasedProtocol implements RandomAdditiveMaskProtocol {

	private int bitLength, securityParameter;
	private SInt[] bits, allBits;
	private SInt mask;
	private InnerProductFactory innerProdFactory;
	private MiscOIntGenerators miscGen;

	
	@Override
	public ProtocolProducer nextProtocolProducer() {
		OInt[] twoPows = miscGen.getTwoPowers(securityParameter + bitLength);
		innerProdFactory.getInnerProductProtocol(bits, twoPows, mask);
		//TODO: FINISH this
		return null;
	}

}
