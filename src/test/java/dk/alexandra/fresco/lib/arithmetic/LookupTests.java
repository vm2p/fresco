package dk.alexandra.fresco.lib.arithmetic;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Assert;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.TestApplication;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThread;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadConfiguration;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadFactory;
import dk.alexandra.fresco.framework.sce.SCE;
import dk.alexandra.fresco.framework.sce.SCEFactory;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.collections.LookUpCircuitFactory;
import dk.alexandra.fresco.lib.collections.LookupProtocolFactoryImpl;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.ParallelProtocolProducer;
import dk.alexandra.fresco.lib.helper.builder.NumericIOBuilder;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;
import dk.alexandra.fresco.lib.lp.LPFactory;
import dk.alexandra.fresco.lib.lp.LPFactoryImpl;
import dk.alexandra.fresco.lib.math.PreprocessedNumericBitFactory;
import dk.alexandra.fresco.lib.math.exp.ExpFromOIntFactory;
import dk.alexandra.fresco.lib.math.exp.PreprocessedExpPipeFactory;
import dk.alexandra.fresco.lib.math.inv.LocalInversionFactory;

public class LookupTests {

	private abstract static class ThreadWithFixture extends TestThread {

		protected SCE sce;

		@Override
		public void setUp() throws IOException {
			sce = SCEFactory.getSCEFromConfiguration(conf.sceConf, conf.protocolSuiteConf);
		}

	}
	
	private static SInt[] getInputFromIntToInt(NumericIOBuilder ioBuilder, int from, int to){
		int[] inputs = new int[to-from];
		int inx = 0;
		for(int i = from; i < to; i++){
			inputs[inx++] = i;
		}
		return ioBuilder.inputArray(inputs, 1);		
	}
	
	private static SInt[] getCleanSIntArr(BasicNumericFactory bnf, int number){
		SInt[] res = new SInt[number];
		for(int i = 0; i < number; i++){
			res[i] = bnf.getSInt(0);	
		}		
		return res;
	}

	public static class TestLookUpProtocol extends TestThreadFactory {		
		
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				@Override
				public void test() throws Exception {
					final int LOOKUPS = 3;
					final OInt[][] outputs = new OInt[LOOKUPS][];
					Application app = new Application() {

						private static final long serialVersionUID = 4338818809103728010L;

						@Override
						public ProtocolProducer prepareApplication(
								ProtocolFactory provider) {
							BasicNumericFactory bnf = (BasicNumericFactory) provider;
							LocalInversionFactory localInvFactory = (LocalInversionFactory) provider;
							PreprocessedNumericBitFactory numericBitFactory = (PreprocessedNumericBitFactory) provider;
							PreprocessedExpPipeFactory expFactory = (PreprocessedExpPipeFactory) provider;
							ExpFromOIntFactory expFromOIntFactory = (ExpFromOIntFactory) provider;
							
							int securityParameter = 80;
							LPFactory lpFactory = new LPFactoryImpl(securityParameter, bnf, localInvFactory, numericBitFactory, expFromOIntFactory, expFactory);
							LookUpCircuitFactory<SInt> lookupFactory = new LookupProtocolFactoryImpl(securityParameter, lpFactory, bnf);
							
							NumericIOBuilder ioBuilder = new NumericIOBuilder(bnf);
							int length = 1000;
							SInt[] cvr_numbers = getInputFromIntToInt(ioBuilder, 1000, 2000);
							SInt[] val1 = getInputFromIntToInt(ioBuilder, 0, 1000);
							SInt[] val2 = getInputFromIntToInt(ioBuilder, 1000, 2000);
							SInt[] val3 = getInputFromIntToInt(ioBuilder, 2000, 3000);
							SInt[][] values = new SInt[length][3];
							for(int i = 0; i < length; i++){
								values[i][0] = val1[i];
								values[i][1] = val2[i];
								values[i][2] = val3[i];
							}							
							
							SequentialProtocolProducer seq = new SequentialProtocolProducer();
							seq.append(ioBuilder.getCircuit());
							
							ParallelProtocolProducer par = new ParallelProtocolProducer();
							for(int i = 0; i < LOOKUPS; i++){
								SInt[] outputValues = getCleanSIntArr(bnf, length);
								SInt lookUpKey = cvr_numbers[i];					
								SequentialProtocolProducer lookupAndOutput = new SequentialProtocolProducer();
								lookupAndOutput.append(lookupFactory.getLookUpCircuit(lookUpKey, cvr_numbers, values, outputValues));
								outputs[i] = ioBuilder.outputArray(outputValues);
								lookupAndOutput.append(ioBuilder.getCircuit());
								ioBuilder.reset();
								par.append(lookupAndOutput);
							}
							seq.append(par);
							return seq;
						}
					};

					sce.runApplication(app);
					
					for(int i = 0; i < LOOKUPS; i++){
						Assert.assertEquals(BigInteger.valueOf(0+i),
								outputs[i][0].getValue());
						Assert.assertEquals(BigInteger.valueOf(1000+i),
								outputs[i][1].getValue());
						Assert.assertEquals(BigInteger.valueOf(2000+i),
								outputs[i][2].getValue());
					}
				}
			};
		}
	}
}
