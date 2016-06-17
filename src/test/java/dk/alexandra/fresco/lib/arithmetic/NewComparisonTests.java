package dk.alexandra.fresco.lib.arithmetic;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Assert;

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
import dk.alexandra.fresco.lib.compare.ComparisonProtocolFactoryImpl;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.ComparisonProtocolBuilder;
import dk.alexandra.fresco.lib.helper.builder.NumericIOBuilder;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;
import dk.alexandra.fresco.lib.math.integer.PreprocessedNumericBitFactory;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFactory;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFromOIntFactory;
import dk.alexandra.fresco.lib.math.integer.exp.PreProExpFactory;
import dk.alexandra.fresco.lib.math.integer.exp.PreprocessedExpPipeFactory;
import dk.alexandra.fresco.lib.math.integer.inv.LocalInversionFactory;
import dk.alexandra.fresco.lib.newcompare.NewCompareFactory;
import dk.alexandra.fresco.lib.newcompare.NewCompareFactoryImpl;

public class NewComparisonTests {
	
	private abstract static class ThreadWithFixture extends TestThread {

		protected SCE sce;

		@Override
		public void setUp() throws IOException {
			sce = SCEFactory.getSCEFromConfiguration(conf.sceConf,
					conf.protocolSuiteConf);
		}

	}
	
	/**
	 * 
	 */
	public static class TestCompareEQ extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				@Override
				public void test() throws Exception {
					TestApplication app = new TestApplication() {

						private static final long serialVersionUID = 4338818809103728010L;
						
						private BigInteger zero = BigInteger.ZERO;
						private BigInteger negFive = BigInteger.valueOf(-5);
						private BigInteger three = BigInteger.valueOf(3);
						private BigInteger five = BigInteger.valueOf(5);
						
						@Override
						public ProtocolProducer prepareApplication(
								ProtocolFactory factory) {
							PreprocessedExpPipeFactory pepFac = (PreprocessedExpPipeFactory)factory;
							PreprocessedNumericBitFactory pnbFac = (PreprocessedNumericBitFactory)factory;
							BasicNumericFactory bnFac = (BasicNumericFactory)factory;
							ExpFromOIntFactory oExpFac = (ExpFromOIntFactory)factory;
							ExpFactory expFac = new PreProExpFactory(pepFac, bnFac, oExpFac);					
							NewCompareFactory compFac = new NewCompareFactoryImpl(bnFac, expFac, pnbFac);							
							NumericIOBuilder ioBuilder = new NumericIOBuilder(bnFac);
							SInt x1 = ioBuilder.input(negFive, 1);
							SInt x2 = ioBuilder.input(zero, 1);
							SInt x3 = ioBuilder.input(three, 1);
							SInt x4 = ioBuilder.input(five, 1);
							SInt r1 = bnFac.getSInt();
							SInt r2 = bnFac.getSInt();
							SInt r3 = bnFac.getSInt();
							SInt r4 = bnFac.getSInt();
							ProtocolProducer comp1 = compFac.getEqualityProtocol(x1, x1, r1, bnFac.getMaxBitLength(), 80);
							ProtocolProducer comp2 = compFac.getEqualityProtocol(x1, x2, r2, 10, 80);
							ProtocolProducer comp3 = compFac.getEqualityProtocol(x3, x4, r3, 10, 80);
							ProtocolProducer comp4 = compFac.getEqualityProtocol(x4, x4, r4, 10, 80);
							ioBuilder.beginParScope();
							ioBuilder.addProtocolProducer(comp1);
							ioBuilder.addProtocolProducer(comp2);
							ioBuilder.addProtocolProducer(comp3);
							ioBuilder.addProtocolProducer(comp4);
							ioBuilder.endCurScope();
							outputs = ioBuilder.outputArray(new SInt[] {r1, r2, r3, r4});
							return ioBuilder.getProtocol();
						}
					};
					sce.runApplication(app);
					Assert.assertEquals(BigInteger.ONE, app.getOutputs()[0].getValue());
					Assert.assertEquals(BigInteger.ZERO, app.getOutputs()[1].getValue());
					Assert.assertEquals(BigInteger.ZERO, app.getOutputs()[2].getValue());
					Assert.assertEquals(BigInteger.ONE, app.getOutputs()[3].getValue());
				}
			};
		}
	}
	
	/**
	 * 
	 */
	public static class TestZeroTest extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				@Override
				public void test() throws Exception {
					TestApplication app = new TestApplication() {

						private static final long serialVersionUID = 4338818809103728010L;
						
						private BigInteger zero = BigInteger.ZERO;
						private BigInteger negFive = BigInteger.valueOf(-5);
						private BigInteger three = BigInteger.valueOf(3);
						private BigInteger five = BigInteger.valueOf(5);
						
						@Override
						public ProtocolProducer prepareApplication(
								ProtocolFactory factory) {
							PreprocessedExpPipeFactory pepFac = (PreprocessedExpPipeFactory)factory;
							PreprocessedNumericBitFactory pnbFac = (PreprocessedNumericBitFactory)factory;
							BasicNumericFactory bnFac = (BasicNumericFactory)factory;
							ExpFromOIntFactory oExpFac = (ExpFromOIntFactory)factory;
							ExpFactory expFac = new PreProExpFactory(pepFac, bnFac, oExpFac);					
							NewCompareFactory compFac = new NewCompareFactoryImpl(bnFac, expFac, pnbFac);							
							NumericIOBuilder ioBuilder = new NumericIOBuilder(bnFac);
							SInt x1 = ioBuilder.input(negFive, 1);
							SInt x2 = ioBuilder.input(zero, 1);
							SInt x3 = ioBuilder.input(three, 1);
							SInt x4 = ioBuilder.input(five, 1);
							SInt r1 = bnFac.getSInt();
							SInt r2 = bnFac.getSInt();
							SInt r3 = bnFac.getSInt();
							SInt r4 = bnFac.getSInt();
							ProtocolProducer comp1 = compFac.getZeroTestProtocol(x1, r1, 10, 80);
							ProtocolProducer comp2 = compFac.getZeroTestProtocol(x2, r2, 10, 80);
							ProtocolProducer comp3 = compFac.getZeroTestProtocol(x3, r3, 10, 80);
							ProtocolProducer comp4 = compFac.getZeroTestProtocol(x4, r4, 10, 80);
							ioBuilder.addProtocolProducer(comp1);
							ioBuilder.addProtocolProducer(comp2);
							ioBuilder.addProtocolProducer(comp3);
							ioBuilder.addProtocolProducer(comp4);
							outputs = ioBuilder.outputArray(new SInt[] {r1, r2, r3, r4});
							return ioBuilder.getProtocol();
						}
					};
					sce.runApplication(app);
					Assert.assertEquals(BigInteger.ZERO, app.getOutputs()[0].getValue());
					Assert.assertEquals(BigInteger.ONE, app.getOutputs()[1].getValue());
					Assert.assertEquals(BigInteger.ZERO, app.getOutputs()[2].getValue());
					Assert.assertEquals(BigInteger.ZERO, app.getOutputs()[3].getValue());
				}
			};
		}
	}


}
