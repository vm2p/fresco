package dk.alexandra.fresco.suite.verifiedyao;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.TestBoolApplication;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThread;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadConfiguration;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadFactory;
import dk.alexandra.fresco.framework.sce.SCE;
import dk.alexandra.fresco.framework.sce.SCEFactory;
import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.lib.crypto.BristolCryptoFactory;
import dk.alexandra.fresco.lib.field.bool.BasicLogicFactory;
import dk.alexandra.fresco.lib.helper.ParallelProtocolProducer;
import dk.alexandra.fresco.lib.helper.bristol.BristolCircuit;
import dk.alexandra.fresco.lib.helper.builder.BasicLogicBuilder;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;
import dk.alexandra.fresco.lib.logic.AbstractBinaryFactory;
import dk.alexandra.fresco.suite.tinytables.prepro.TinyTablesPreproConfiguration;

public class VerYaoTests {
	
	private abstract static class ThreadWithFixture extends TestThread {


		protected SCE sce;

		@Override
		public void setUp() throws IOException {
			sce = SCEFactory.getSCEFromConfiguration(conf.sceConf, conf.protocolSuiteConf);				
		}

	}
	
	private static boolean[] toBoolean(String hex) throws IllegalArgumentException {
		if (hex.length() % 2 != 0) {
			throw new IllegalArgumentException("Illegal hex string");
		}
		boolean[] res = new boolean[hex.length() * 4];
		for (int i=0; i<hex.length() / 2; i++) {
			String sub = hex.substring(2*i,2*i +2);
			int value = Integer.parseInt(sub, 16);
			int numOfBits = 8;
			for (int j = 0; j < numOfBits; j++) {
				boolean val = (value & 1 << j) != 0;
		        res[8*i + (numOfBits - j - 1)] = val;
		    }
		}
		return res;
	}
	
	private static String fromBoolean(boolean[] b) throws IllegalArgumentException {
		
		String res = "";
		for (int i=0; i<b.length; i++) {
			res = res + (b[i] ? 1 : 0);
		}
		return res;
	}
	
	public static class Add32x32Test extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				SBool[] in1, in2, out;
				OBool[] openedOut;
				
				String inv1 = "00000000";
				String inv2 = "00000000";
				String outv = "00000000";
				@Override
				public void test() throws Exception {
					Application multApp = new Application() {

						private static final long serialVersionUID = 36363636L;

						@Override
						public ProtocolProducer prepareApplication(ProtocolFactory fac) {
							BasicLogicFactory bool = (BasicLogicFactory)fac;
							
							boolean[] in1_val = toBoolean(inv1);
							in1 = bool.getKnownConstantSBools(in1_val);
							boolean[] in2_val = toBoolean(inv2);
							in2 = bool.getKnownConstantSBools(in2_val);
							
							VerYaoConfiguration.li1 = 32;
							VerYaoConfiguration.li2 = 32;
							
							if (conf.netConf.getMyId() == 1) {
								VerYaoConfiguration.i1.append("00000000000000000000000000000000");
							}
							else {
								VerYaoConfiguration.i2.append("00000000000000000000000000000000");
							}
							out = bool.getSBools(33);

							// Create mult circuit.
							BristolCryptoFactory multFac = new BristolCryptoFactory(bool);
							BristolCircuit mult = multFac.getAdd32x32Circuit(in1, in2, out);
							
							// Create circuits for opening result of 32x32 bit mult.
							ProtocolProducer[] opens = new ProtocolProducer[out.length];
							openedOut = new OBool[out.length];
							for (int i=0; i<out.length; i++) {
								openedOut[i] = bool.getOBool();
								opens[i] = bool.getOpenProtocol(out[i], openedOut[i]);
							}
							ProtocolProducer open_all = new ParallelProtocolProducer(opens);
							
							return new SequentialProtocolProducer(mult, open_all);
						}
					};

					sce.runApplication(multApp);

					if (conf.protocolSuiteConf instanceof TinyTablesPreproConfiguration) {
						// Do nothing
					} else {
						boolean[] expected = ArrayUtils.addAll(toBoolean(outv), new boolean[]{false});
						boolean[] actual = new boolean[out.length];
						for (int i=0; i<out.length; i++) {
							actual[i] = openedOut[i].getValue();
						}
	
						//					System.out.println("IN1        : " + Arrays.toString(toBoolean(inv1)));
						//					System.out.println("IN2        : " + Arrays.toString(toBoolean(inv2)));
						//					System.out.println("EXPECTED   : " + Arrays.toString(expected));
						//					System.out.println("ACTUAL     : " + Arrays.toString(actual));
						
						Assert.assertTrue(Arrays.equals(expected, actual));
					}
					
				}
			};
		}
	}

	public static class Mult32x32Test extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				SBool[] in1, in2, out;
				OBool[] openedOut;
				
				String inv1 = "00000000";
				String inv2 = "00000000";
				String outv = "0000000000000000";
				@Override
				public void test() throws Exception {
					Application multApp = new Application() {

						private static final long serialVersionUID = 36363636L;

						@Override
						public ProtocolProducer prepareApplication(ProtocolFactory fac) {
							BasicLogicFactory bool = (BasicLogicFactory)fac;
							
							boolean[] in1_val = toBoolean(inv1);
							in1 = bool.getKnownConstantSBools(in1_val);
							boolean[] in2_val = toBoolean(inv2);
							in2 = bool.getKnownConstantSBools(in2_val);

							VerYaoConfiguration.li1 = 32;
							VerYaoConfiguration.li2 = 32;
							
							if (conf.netConf.getMyId() == 1) {
								VerYaoConfiguration.i1.append("00000000000000000000000000000000");
							}
							else {
								VerYaoConfiguration.i2.append("00000000000000000000000000000000");
							}
							
							out = bool.getSBools(64);

							// Create mult circuit.
							BristolCryptoFactory multFac = new BristolCryptoFactory(bool);
							BristolCircuit mult = multFac.getMult32x32Circuit(in1, in2, out);
							
							// Create circuits for opening result of 32x32 bit mult.
							ProtocolProducer[] opens = new ProtocolProducer[out.length];
							openedOut = new OBool[out.length];
							for (int i=0; i<out.length; i++) {
								openedOut[i] = bool.getOBool();
								opens[i] = bool.getOpenProtocol(out[i], openedOut[i]);
							}
							ProtocolProducer open_all = new ParallelProtocolProducer(opens);
							
							return new SequentialProtocolProducer(mult, open_all);
						}
					};

					sce.runApplication(multApp);

					if (conf.protocolSuiteConf instanceof TinyTablesPreproConfiguration) {
						// Do nothing
					} else {
						boolean[] expected = toBoolean(outv);
						boolean[] actual = new boolean[out.length];
						for (int i=0; i<out.length; i++) {
							actual[i] = openedOut[i].getValue();
						}
	
						//					System.out.println("IN1        : " + Arrays.toString(toBoolean(inv1)));
						//					System.out.println("IN2        : " + Arrays.toString(toBoolean(inv2)));
						//					System.out.println("EXPECTED   : " + Arrays.toString(expected));
						//					System.out.println("ACTUAL     : " + Arrays.toString(actual));
						
						Assert.assertTrue(Arrays.equals(expected, actual));
					}
					
				}
			};
		}
	}
	
	public static class AesTest extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				
				// This is just some fixed test vectors for AES in ECB mode that was
				// found somewhere on the net, i.e., this is some known plaintexts and
				// corresponding cipher texts that can be used for testing.
				final String[] keyVec = new String[] { "000102030405060708090a0b0c0d0e0f"};
				final String plainVec = "00112233445566778899aabbccddeeff";
				final String[] cipherVec = new String[] { "69c4e0d86a7b0430d8cdb78070b4c55a"};
				
				SBool[] plain, key, cipher;
				OBool[] openedCipher;
				
				@Override
				public void test() throws Exception {
					Application aesApp = new Application() {

						private static final long serialVersionUID = 1923498347L;

						@Override
						public ProtocolProducer prepareApplication(ProtocolFactory fac) {
							AbstractBinaryFactory prov = (AbstractBinaryFactory) fac;
							BasicLogicBuilder builder = new BasicLogicBuilder(prov);
							
							boolean[] key_val = toBoolean(keyVec[0]);
							boolean[] in_val = toBoolean(plainVec);
							
							plain = prov.getKnownConstantSBools(in_val);
							if (conf.netConf.getMyId() == 1) VerYaoConfiguration.i1.append(fromBoolean(in_val));
							VerYaoConfiguration.li1 = 128;
							key = prov.getKnownConstantSBools(key_val);
							if (conf.netConf.getMyId() == 2) VerYaoConfiguration.i2.append(fromBoolean(key_val));
							VerYaoConfiguration.li2 = 128;
							cipher = prov.getSBools(128);

							// Create AES circuit.
							BristolCryptoFactory aesFac = new BristolCryptoFactory(prov);
							BristolCircuit aes = aesFac.getAesProtocol(plain, key, cipher);
							builder.addProtocolProducer(aes);
							
							// Create circuits for opening result of AES.							
							openedCipher = builder.output(cipher);
							
							return new SequentialProtocolProducer(builder.getProtocol());
						}
					};

					sce.runApplication(aesApp);
					
					if (conf.protocolSuiteConf instanceof TinyTablesPreproConfiguration) {
						// Just preprocessing - do not check output
					} else {
						boolean[] expected = toBoolean(cipherVec[0]);
						boolean[] actual = new boolean[128];
						for (int i=0; i<128; i++) {
							actual[i] = openedCipher[i].getValue();
						}
						
						//					System.out.println("KEY       : " + Arrays.toString(toBoolean(keyVec)));
						//					System.out.println("IN        : " + Arrays.toString(toBoolean(inVec[0])));
						//					System.out.println("EXPECTED  : " + Arrays.toString(expected));
						//					System.out.println("ACTUAL OPN: " + Arrays.toString(actual));
						
						Assert.assertTrue(Arrays.equals(expected, actual));
					}
				}
			};
		}
	}
	
	public static class DesTest extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				SBool[] plain, key, cipher;
				OBool[] openedOut;
				
				String keyV = "0101010101010101";
				String plainV = "8000000000000000";
				String cipherV = "95F8A5E5DD31D900".toLowerCase();
				@Override
				public void test() throws Exception {
					Application md5App = new Application() {

						private static final long serialVersionUID = 36625566;

						@Override
						public ProtocolProducer prepareApplication(ProtocolFactory fac) {
							BasicLogicFactory bool = (BasicLogicFactory)fac;

							boolean[] in1_val = toBoolean(plainV);
							plain = bool.getKnownConstantSBools(in1_val);
							boolean[] in2_val = toBoolean(keyV);
							key = bool.getKnownConstantSBools(in2_val);
							cipher = bool.getSBools(64);
							
							if (conf.netConf.getMyId() == 1) {
								VerYaoConfiguration.i1.append(fromBoolean(in1_val));
								VerYaoConfiguration.li1 = VerYaoConfiguration.i1.length();
							}
							else {
								VerYaoConfiguration.i2.append(fromBoolean(in2_val));
								VerYaoConfiguration.li2 = VerYaoConfiguration.i2.length();
							}

							// Create des circuit.
							BristolCryptoFactory desFac = new BristolCryptoFactory(bool);
							BristolCircuit des = desFac.getDesCircuit(plain, key, cipher);
							
							// Create circuits for opening result of DES.
							ProtocolProducer[] opens = new ProtocolProducer[cipher.length];
							openedOut = new OBool[cipher.length];
							for (int i=0; i<cipher.length; i++) {
								openedOut[i] = bool.getOBool();
								opens[i] = bool.getOpenProtocol(cipher[i], openedOut[i]);
							}
							ProtocolProducer open_all = new ParallelProtocolProducer(opens);
							
							return new SequentialProtocolProducer(des, open_all);
						}
					};

					sce.runApplication(md5App);

					if (conf.protocolSuiteConf instanceof TinyTablesPreproConfiguration) {
						// Do nothing
					} else {
						boolean[] expected = toBoolean(cipherV);
						boolean[] actual = new boolean[cipher.length];
						for (int i=0; i<cipher.length; i++) {
							actual[i] = openedOut[i].getValue();
						}
	
						//					System.out.println("IN1        : " + Arrays.toString(toBoolean(inv1)));
						//					System.out.println("IN2        : " + Arrays.toString(toBoolean(inv2)));
						//					System.out.println("EXPECTED   : " + Arrays.toString(expected));
						//					System.out.println("ACTUAL     : " + Arrays.toString(actual));
						
						Assert.assertTrue(Arrays.equals(expected, actual));
					}
					
				}
			};
		}
	}
	
	public static class Sha256Test extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				SBool[] in, out;
				OBool[] openedOut;
				
				/*
				 * IMPORTANT: These are NOT test vectors for the complete SHA-256
				 * hash function, as the padding rules are ignored. Therefore,
				 * use of tools like md5sum will produce a different output if
				 * supplied with the same inputs.
				 */
				String in1 ="00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
				String out1 = "da5698be17b9b46962335799779fbeca8ce5d491c0d26243bafef9ea1837a9d8";
				String in2 = "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f";
				String out2 = "fc99a2df88f42a7a7bb9d18033cdc6a20256755f9d5b9a5044a9cc315abe84a7";
				String in3 = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
				String out3 = "ef0c748df4da50a8d6c43c013edc3ce76c9d9fa9a1458ade56eb86c0a64492d2";
				String in4 = "243f6a8885a308d313198a2e03707344a4093822299f31d0082efa98ec4e6c89452821e638d01377be5466cf34e90c6cc0ac29b7c97c50dd3f84d5b5b5470917";
				String out4 = "cf0ae4eb67d38ffeb94068984b22abde4e92bc548d14585e48dca8882d7b09ce";
				
				@Override
				public void test() throws Exception {
					Application sha256App = new Application() {

						private static final long serialVersionUID = 984759485L;

						@Override
						public ProtocolProducer prepareApplication(ProtocolFactory fac) {
							BasicLogicFactory bool = (BasicLogicFactory)fac;

							boolean[] in_val = toBoolean(in1);
							in = bool.getKnownConstantSBools(in_val);
							out = bool.getSBools(256);

							VerYaoConfiguration.li1 = 512;
							VerYaoConfiguration.li2 = 0;
							
							if (conf.netConf.getMyId() == 1) {
								VerYaoConfiguration.i1.append(fromBoolean(in_val));
							}
							else {
								VerYaoConfiguration.i2.append("");
							}
							
							// Create SHA1 circuit.
							BristolCryptoFactory sha256Fac = new BristolCryptoFactory(bool);
							BristolCircuit sha256 = sha256Fac.getSha256Circuit(in, out);
							
							// Create circuits for opening result of SHA 256.
							ProtocolProducer[] opens = new ProtocolProducer[out.length];
							openedOut = new OBool[out.length];
							for (int i=0; i<out.length; i++) {
								openedOut[i] = bool.getOBool();
								opens[i] = bool.getOpenProtocol(out[i], openedOut[i]);
							}
							ProtocolProducer open_all = new ParallelProtocolProducer(opens);
							
							return new SequentialProtocolProducer(sha256, open_all);
						}
					};

					sce.runApplication(sha256App);

					if (conf.protocolSuiteConf instanceof TinyTablesPreproConfiguration) {
						// Do nothing
					} else {
						boolean[] expected = toBoolean(out1);
						boolean[] actual = new boolean[out.length];
						for (int i=0; i<out.length; i++) {
							actual[i] = openedOut[i].getValue();
						}
	
						//					System.out.println("IN        : " + Arrays.toString(AesTests.toBoolean(in1)));
						//					System.out.println("EXPECTED  : " + Arrays.toString(expected));
						//					System.out.println("ACTUAL    : " + Arrays.toString(actual));
						
						Assert.assertTrue(Arrays.equals(expected, actual));
					}					
				}
			};
		}
	}
	
	
	/**
	 * TestingMD5 compression function.
	 * 
	 * TODO: Include all three test vectors.
	 *
	 */
	public static class MD5Test extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				SBool[] in, out;
				OBool[] openedOut;
				
				/*
				 * IMPORTANT: These are NOT test vectors for the complete SHA-1
				 * hash function, as the padding rules are ignored. Therefore,
				 * use of tools like md5sum will produce a different output if
				 * supplied with the same inputs.
				 */
				String in1 ="00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
				String out1 = "ac1d1f03d08ea56eb767ab1f91773174";
				String in2 = "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f";
				String out2 = "cad94491c9e401d9385bfc721ef55f62";
				String in3 = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
				String out3 = "b487195651913e494b55c6bddf405c01";
				String in4 = "243f6a8885a308d313198a2e03707344a4093822299f31d0082efa98ec4e6c89452821e638d01377be5466cf34e90c6cc0ac29b7c97c50dd3f84d5b5b5470917";
				String out4 = "3715f568f422db75cc8d65e11764ff01";
				
				@Override
				public void test() throws Exception {
					Application md5App = new Application() {

						private static final long serialVersionUID = 984759485L;

						@Override
						public ProtocolProducer prepareApplication(ProtocolFactory fac) {
							BasicLogicFactory bool = (BasicLogicFactory)fac;

							boolean[] in_val = toBoolean(in1);
							in = bool.getKnownConstantSBools(in_val);
							out = bool.getSBools(128);

							VerYaoConfiguration.li1 = 512;
							VerYaoConfiguration.li2 = 0;
							
							if (conf.netConf.getMyId() == 1) {
								VerYaoConfiguration.i1.append(fromBoolean(in_val));
							}
							else {
								VerYaoConfiguration.i2.append("");
							}
							
							
							// Create MD5 circuit.
							BristolCryptoFactory md5Fac = new BristolCryptoFactory(bool);
							BristolCircuit md5 = md5Fac.getMD5Circuit(in, out);
							
							// Create circuits for opening result of MD5.
							ProtocolProducer[] opens = new ProtocolProducer[out.length];
							openedOut = new OBool[out.length];
							for (int i=0; i<out.length; i++) {
								openedOut[i] = bool.getOBool();
								opens[i] = bool.getOpenProtocol(out[i], openedOut[i]);
							}
							ProtocolProducer open_all = new ParallelProtocolProducer(opens);
							
							return new SequentialProtocolProducer(md5, open_all);
						}
					};

					sce.runApplication(md5App);

					if (conf.protocolSuiteConf instanceof TinyTablesPreproConfiguration) {
						// Do nothing
					} else {
						boolean[] expected = toBoolean(out1);
						boolean[] actual = new boolean[out.length];
						for (int i=0; i<out.length; i++) {
							actual[i] = openedOut[i].getValue();
						}
	
						//					System.out.println("IN        : " + Arrays.toString(AesTests.toBoolean(in1)));
						//					System.out.println("EXPECTED  : " + Arrays.toString(expected));
						//					System.out.println("ACTUAL    : " + Arrays.toString(actual));
						
						Assert.assertTrue(Arrays.equals(expected, actual));
					}
				}
			};
		}
	}
	
	public static class Sha1Test extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				SBool[] in, out;
				OBool[] openedOut;
				
				/*
				 * IMPORTANT: These are NOT test vectors for the complete SHA-1
				 * hash function, as the padding rules are ignored. Therefore,
				 * use of tools like md5sum will produce a different output if
				 * supplied with the same inputs.
				 */
				String in1 ="00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
				String out1 = "92b404e556588ced6c1acd4ebf053f6809f73a93";
				String in2 = "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f";
				String out2 = "b9ac757bbc2979252e22727406872f94cbea56a1";
				String in3 = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
				String out3 = "bafbc2c87c33322603f38e06c3e0f79c1f1b1475";
				
				@Override
				public void test() throws Exception {
					Application aesApp = new Application() {

						private static final long serialVersionUID = 984759485L;

						@Override
						public ProtocolProducer prepareApplication(ProtocolFactory fac) {
							BasicLogicFactory bool = (BasicLogicFactory)fac;

							boolean[] in_val = toBoolean(in1);
							in = bool.getKnownConstantSBools(in_val);
							out = bool.getSBools(160);

							VerYaoConfiguration.li1 = 512;
							VerYaoConfiguration.li2 = 0;
							
							if (conf.netConf.getMyId() == 1) {
								VerYaoConfiguration.i1.append(fromBoolean(in_val));
							}
							else {
								VerYaoConfiguration.i2.append("");
							}
							
							// Create SHA1 circuit.
							BristolCryptoFactory sha1Fac = new BristolCryptoFactory(bool);
							BristolCircuit aes = sha1Fac.getSha1Circuit(in, out);
							
							// Create circuits for opening result of AES.
							ProtocolProducer[] opens = new ProtocolProducer[out.length];
							openedOut = new OBool[out.length];
							for (int i=0; i<out.length; i++) {
								openedOut[i] = bool.getOBool();
								opens[i] = bool.getOpenProtocol(out[i], openedOut[i]);
							}
							ProtocolProducer open_all = new ParallelProtocolProducer(opens);
							
							return new SequentialProtocolProducer(aes, open_all);
						}
					};

					sce.runApplication(aesApp);

					if (conf.protocolSuiteConf instanceof TinyTablesPreproConfiguration) {
						// Do nothing
					} else {
						boolean[] expected = toBoolean(out1);
						boolean[] actual = new boolean[out.length];
						for (int i=0; i<out.length; i++) {
							actual[i] = openedOut[i].getValue();
						}
	
						//					System.out.println("IN        : " + Arrays.toString(AesTests.toBoolean(in1)));
						//					System.out.println("EXPECTED  : " + Arrays.toString(expected));
						//					System.out.println("ACTUAL    : " + Arrays.toString(actual));
						
						Assert.assertTrue(Arrays.equals(expected, actual));
					}
					
				}
			};
		}
	}
	
	public static class TestGreaterThan extends TestThreadFactory {
		@Override
		public TestThread next(TestThreadConfiguration conf) {
			return new ThreadWithFixture() {
				@Override
				public void test() throws Exception {
					boolean[] comp1 = new boolean[] {false, true, false, true, false};
					boolean[] comp2 = new boolean[] {false, true, true, true, false};
					
					TestBoolApplication app = new TestBoolApplication() {

						private static final long serialVersionUID = 4338818809103728010L;

						@Override
						public ProtocolProducer prepareApplication(
								ProtocolFactory provider) {
							AbstractBinaryFactory prov = (AbstractBinaryFactory) provider;
							BasicLogicBuilder builder = new BasicLogicBuilder(prov);
							
							SBool[] in1 = builder.knownSBool(comp1);
							VerYaoConfiguration.i1.append(fromBoolean(comp1));
							VerYaoConfiguration.li1 = VerYaoConfiguration.i1.length();
							SBool[] in2 = builder.knownSBool(comp2);
							VerYaoConfiguration.i2.append(fromBoolean(comp2));
							VerYaoConfiguration.li2 = VerYaoConfiguration.i2.length();
							
							SBool compRes1 = builder.greaterThan(in1, in2);
							SBool compRes2 = builder.greaterThan(in2, in1);
							
							OBool[] output = new OBool[]{builder.output(compRes1), builder.output(compRes2)};
							this.outputs = output;
							return builder.getProtocol();
						}
					};

					sce.runApplication(app);

					if (conf.protocolSuiteConf instanceof TinyTablesPreproConfiguration) {
						// Just preprocessing - do not check output
					} else {
						System.out.println(app.getOutputs()[0].hashCode());
						Assert.assertEquals(false,
								app.getOutputs()[0].getValue());
						Assert.assertEquals(true,
								app.getOutputs()[1].getValue());
					}
				}
			};
		}
	}
}
