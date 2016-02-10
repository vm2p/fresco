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
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.NumericIOBuilder;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;

public class LookupTests {

	private abstract static class ThreadWithFixture extends TestThread {

		protected SCE sce;

		@Override
		public void setUp() throws IOException {
			sce = SCEFactory.getSCEFromConfiguration(conf.sceConf, conf.protocolSuiteConf);
		}

	}

	public static class TestInput extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				@Override
				public void test() throws Exception {
					TestApplication app = new TestApplication() {

						private static final long serialVersionUID = 4338818809103728010L;

						@Override
						public ProtocolProducer prepareApplication(
								ProtocolFactory provider) {
							BasicNumericFactory prov = (BasicNumericFactory) provider;
							NumericIOBuilder ioBuilder = new NumericIOBuilder(
									prov);
							SInt input1 = ioBuilder.input(BigInteger.valueOf(10), 1);

							OInt output = ioBuilder.output(input1);
							ProtocolProducer io = ioBuilder.getCircuit();

							ProtocolProducer gp = new SequentialProtocolProducer(
									io);
							this.outputs = new OInt[] { output };
							return gp;
						}
					};

					sce.runApplication(app);

					Assert.assertEquals(BigInteger.valueOf(10),
							app.getOutputs()[0].getValue());
				}
			};
		}
	}
}
