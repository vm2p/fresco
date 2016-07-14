package dk.alexandra.fresco.demo;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.junit.Test;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.ProtocolEvaluator;
import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.TestThreadRunner;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThread;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadConfiguration;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadFactory;
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration;
import dk.alexandra.fresco.framework.configuration.TestConfiguration;
import dk.alexandra.fresco.framework.sce.SCE;
import dk.alexandra.fresco.framework.sce.SCEFactory;
import dk.alexandra.fresco.framework.sce.configuration.ProtocolSuiteConfiguration;
import dk.alexandra.fresco.framework.sce.configuration.TestSCEConfiguration;
import dk.alexandra.fresco.framework.sce.evaluator.ParallelEvaluator;
import dk.alexandra.fresco.framework.sce.resources.storage.InMemoryStorage;
import dk.alexandra.fresco.framework.sce.resources.storage.Storage;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.suite.bgw.BgwProtocolSuite;
import dk.alexandra.fresco.suite.bgw.configuration.BgwConfiguration;
import dk.alexandra.fresco.suite.spdz.configuration.SpdzConfiguration;
import dk.alexandra.fresco.suite.spdz.evaluation.strategy.SpdzProtocolSuite;

public class DemoApp implements Application{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6586720800507231446L;

	private OInt result;
	
	@Override
	public ProtocolProducer prepareApplication(ProtocolFactory provider) {
		//Assume basic numeric functionality
		BasicNumericFactory bnf = (BasicNumericFactory) provider;
		
		//Create a builder similar to the BigInteger interface
		NumericProtocolBuilder builder = new NumericProtocolBuilder(bnf);
		
		//input some cleartext to make it secret shared between the parties. Party 1 inputs here.
		SInt[] inputs = builder.inputArray(new int[] {22, 46}, 1);
		
		//Do some function on the secret shares - here the sum
		SInt sum = builder.sum(inputs);
		
		//Open a value
		OInt open = builder.output(sum);
		this.result = open;
		
		//Get the underlying circuit representing the functionality we implemented.
		return builder.getProtocol();
	}
	
	public OInt getOutput() {
		return this.result;
	}
	
	private abstract class ThreadWithFixture extends TestThread {

		protected SCE sce;
		
		@Override
		public void setUp() throws IOException {			
			ProtocolEvaluator evaluator = new ParallelEvaluator();
			Storage storage = new InMemoryStorage();
			ProtocolSuiteConfiguration psConf;
			//Using BGW
			psConf = new BgwConfiguration() {
				
				@Override
				public int getThreshold() {
					return 1; 
				}
				
				@Override
				public BigInteger getModulus() {
					return new BigInteger("2147483647");
				}
			};
			sce = SCEFactory.getSCEFromConfiguration((new TestSCEConfiguration(BgwProtocolSuite.getInstance(), evaluator, 3, 3, conf.netConf, storage, true)), psConf);

			//Using SPDZ
			
			psConf = new SpdzConfiguration() {
				
				@Override
				public boolean useDummyData() {
					return true;
				}
				
				@Override
				public String getTriplePath() {
					return null;
				}
				
				@Override
				public int getMaxBitLength() {
					return 40;
				}
			};
			sce = SCEFactory.getSCEFromConfiguration((new TestSCEConfiguration(new SpdzProtocolSuite(), evaluator, 3, 3, conf.netConf, storage, true)), psConf);
			
		}

	}
	private static void runTest(TestThreadFactory test, int n) {
		// Since SCAPI currently does not work with ports > 9999 we use fixed ports
		// here instead of relying on ephemeral ports which are often > 9999.
		List<Integer> ports = new ArrayList<Integer>(n);
		for (int i=1; i<=n; i++) {
			ports.add(39000 + i);
		}
		Map<Integer, NetworkConfiguration> netConf = TestConfiguration.getNetworkConfigurations(n, ports, Level.FINE);
		Map<Integer, TestThreadConfiguration> conf = new HashMap<Integer, TestThreadConfiguration>();
		for (int i : netConf.keySet()) {
			TestThreadConfiguration ttc = new TestThreadConfiguration();
			ttc.netConf = netConf.get(i);
			conf.put(i, ttc);
		}
		TestThreadRunner.run(test, conf);

	}
	
	@Test
	public void testDemoApp() throws Exception {
		final TestThreadFactory f = new TestThreadFactory() {
			@Override
			public TestThread next(TestThreadConfiguration conf) {
				return new ThreadWithFixture() {
					@Override
					public void test() throws Exception {
						DemoApp app = new DemoApp();
						this.sce.runApplication(app);	
						
						OInt result = app.getOutput();
						System.out.println("Result of the computation was: " + result.getValue());
					}
				};
			};
		};		
		runTest(f, 3);
	}
	

}
