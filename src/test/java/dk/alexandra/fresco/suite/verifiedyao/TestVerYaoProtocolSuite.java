package dk.alexandra.fresco.suite.verifiedyao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.junit.Test;

import dk.alexandra.fresco.framework.ProtocolEvaluator;
import dk.alexandra.fresco.framework.Reporter;
import dk.alexandra.fresco.framework.TestThreadRunner;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadConfiguration;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadFactory;
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration;
import dk.alexandra.fresco.framework.configuration.TestConfiguration;
import dk.alexandra.fresco.framework.sce.configuration.TestSCEConfiguration;
import dk.alexandra.fresco.framework.sce.evaluator.EvaluationStrategy;
import dk.alexandra.fresco.framework.sce.resources.storage.InMemoryStorage;
import dk.alexandra.fresco.framework.sce.resources.storage.Storage;
import dk.alexandra.fresco.suite.ProtocolSuite;


/**
 * Various tests of the dummy protocol suite.
 * 
 * Currently, we simply test that AES works using the dummy protocol suite.
 * 
 */
public class TestVerYaoProtocolSuite {

	private void runTest(TestThreadFactory f, EvaluationStrategy evalStrategy) throws Exception {
		int noPlayers = 2;
		Level logLevel = Level.FINE;
		Reporter.init(logLevel);
		
		// Since SCAPI currently does not work with ports > 9999 we use fixed ports
		// here instead of relying on ephemeral ports which are often > 9999.
		List<Integer> ports = new ArrayList<Integer>(noPlayers);
		for (int i=1; i<=noPlayers; i++) {
			ports.add(9000 + i);
		}
		
		Map<Integer, NetworkConfiguration> netConf = TestConfiguration.getNetworkConfigurations(noPlayers, ports, logLevel);
		Map<Integer, TestThreadConfiguration> conf = new HashMap<Integer, TestThreadConfiguration>();
		for (int playerId : netConf.keySet()) {
			TestThreadConfiguration ttc = new TestThreadConfiguration();
			ttc.netConf = netConf.get(playerId);
			ttc.protocolSuiteConf = new VerYaoConfiguration();
			boolean useSecureConnection = false; // No tests of secure connection here.
			int noOfVMThreads = 3;
			int noOfThreads = 3;
			ProtocolSuite protocolSuite = new VerYaoProtocolSuite();
			ProtocolEvaluator evaluator = EvaluationStrategy.fromEnum(evalStrategy);
			Storage storage = new InMemoryStorage();
			ttc.sceConf = new TestSCEConfiguration(protocolSuite, evaluator, noOfThreads, noOfVMThreads, ttc.netConf, storage, useSecureConnection);
			conf.put(playerId, ttc);			
		}
		TestThreadRunner.run(f, conf);
	}
	
	/***/
	
	
	@Test
	public void test_Add32x32_Sequential() throws Exception {
		runTest(new VerYaoTests.Add32x32Test(), EvaluationStrategy.SEQUENTIAL);
	}
	
	@Test
	public void test_Mult32x32_Sequential() throws Exception {
		runTest(new VerYaoTests.Mult32x32Test(), EvaluationStrategy.SEQUENTIAL);
	}
	
	@Test
	public void test_AES_Sequential() throws Exception {
		runTest(new VerYaoTests.AesTest(), EvaluationStrategy.SEQUENTIAL);
	}
	
	@Test
	public void test_DES_Sequential() throws Exception {
		runTest(new VerYaoTests.DesTest(), EvaluationStrategy.SEQUENTIAL);
	}
	
	@Test
	public void test_SHA1_Sequential() throws Exception {
		runTest(new VerYaoTests.Sha1Test(), EvaluationStrategy.SEQUENTIAL);
	}

	@Test
	public void test_SHA256_Sequential() throws Exception {
		runTest(new VerYaoTests.Sha256Test(), EvaluationStrategy.SEQUENTIAL);
	}

}
