package dk.alexandra.fresco.lib.newcompare.subprotocols;

import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.RandomAdditiveMaskProtocol;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestBruteforceProtocol;
import dk.alexandra.fresco.lib.compare.zerotest.ZeroTestReducerProtocol;
import dk.alexandra.fresco.lib.math.integer.HammingDistanceProtocol;

public interface ComparisonSubProtocolFactory extends ProtocolFactory {
	
	public ZeroTestReducerProtocol getZTReducerProtocol(SInt x, SInt result, int bitLength, int securityParameter);
	
	public HammingDistanceProtocol getZTHammingDistanceProtocol(SInt[] xs, OInt y, SInt result);
	
	public ZeroTestBruteforceProtocol getZTBruteForceProtocol(SInt x, SInt result, int max);

	public RandomAdditiveMaskProtocol getZTMaskProtocol(SInt[] bits, SInt mask, int securityParameter);
}
