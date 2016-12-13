package dk.alexandra.fresco.suite.verifiedyao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import dk.alexandra.fresco.framework.MPCException;
import dk.alexandra.fresco.framework.NativeProtocol.EvaluationStatus;
import dk.alexandra.fresco.framework.sce.configuration.ProtocolSuiteConfiguration;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.suite.ProtocolSuite;
import dk.alexandra.fresco.framework.network.*;

public class VerYaoProtocolSuite implements ProtocolSuite {
	
	private Network network;
	private ResourcePool rp;
	private SCENetworkImpl protocolNetwork;
	
	public VerYaoProtocolSuite() {
	}
	
	@Override
	public void init(ResourcePool resourcePool, ProtocolSuiteConfiguration conf) {

		this.network = resourcePool.getNetwork();
		this.rp = resourcePool;
		this.protocolNetwork = new SCENetworkImpl(this.rp.getNoOfParties(), 0);
	}

	@Override
	public void synchronize(int gatesEvaluated) throws MPCException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void finishedEval() {
		VerYaoEvalProtocol eval = new VerYaoEvalProtocol();
		
		EvaluationStatus status;
		int i = 0;
		do {
			status = eval.evaluate(i, this.rp, protocolNetwork);
			i++;
			// send phase
			Map<Integer, Queue<Serializable>> output = protocolNetwork.getOutputFromThisRound();
			for (int pId : output.keySet()) {
				// send array since queue is not serializable
				try {
					network.send("0", pId, output.get(pId).toArray(new Serializable[0]));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// receive phase
			Map<Integer, Queue<Serializable>> inputForThisRound = new HashMap<Integer, Queue<Serializable>>();
			for (int pId : protocolNetwork.getExpectedInputForNextRound()) {
				Serializable[] messages = null;
				try {
					messages = network.receive("0", pId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Queue<Serializable> q = new LinkedBlockingQueue<Serializable>();
				// convert back from array to queue.
				for (Serializable message : messages) {
					q.offer(message);
				}
				inputForThisRound.put(pId, q);
			}
			protocolNetwork.setInput(inputForThisRound);
			protocolNetwork.nextRound();
		} while (status != EvaluationStatus.IS_DONE);
		
		System.out.println("OUTPUT = " + VerYaoConfiguration.output);
		
		for (i = 0; i < VerYaoConfiguration.output.length(); i ++) {
			if (VerYaoConfiguration.output.charAt(i) == '1') {
				VerYaoConfiguration.output1.get(i).setValue(true);
				VerYaoConfiguration.output2.get(i).setValue(true);
			}
			else {
			VerYaoConfiguration.output1.get(i).setValue(false);
			VerYaoConfiguration.output2.get(i).setValue(false);
			}
		}
				
		/*
		 * Clean
		 * */
		File f = new File("p2stage1.dat");
		f.delete();
		f = new File("p1stage1.dat");
		f.delete();
		f = new File("p2stage2.dat");
		f.delete();
		f = new File("p1stage2.dat");
		f.delete();
		f = new File("state1.dat");
		f.delete();
		f = new File("state2.dat");
		f.delete();
		f = new File("state2simpl.dat");
		f.delete();
		
	}

}
