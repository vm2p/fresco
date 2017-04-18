package dk.alexandra.fresco.suite.verifiedyao;

import java.io.File;
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

/**
 * Verified Yao Protocol Suite.
 * 
 * This suite evaluates boolean circuits according to
 * the input of two parties using a verified implementation
 * of Yao's SFE protocol.
 * */

public class VerYaoProtocolSuite implements ProtocolSuite {
	
	private Network network;
	private ResourcePool rp;
	private SCENetworkImpl protocolNetwork;
	
	/**
	 * Builds a protocol suite that will evaluate circuits
	 * using the Yao SFE protocol.
	 * */
	public VerYaoProtocolSuite() {
	}
	
	@Override
	/**
	 * At the beginning of the evaluation, we need to store the network,
	 * the resourcePool and the protocol network that will be used during
	 * the evaluation, so that we are able to correctly create a 
	 * VerYaoEvalProtocol gate that will perform all the computation.
	 * 
	 * @param resourcePool
	 * 		resourcePool to be used in the evaluation
	 * @param conf
	 * 		protocol suite configuration
	 * */
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
	/**
	 * Finishes the FRESCO evaluation part (that builds the circuit) and
	 * starts the OCaml evaluation part (where the actual evaluation happens).
	 * 
	 * The method creates a VerYaoEvalProtocol gate that will perform
	 * all the necessary queries to the OCaml evaluator and will evaluate
	 * that gate until it returns that the process is complete.
	 * 
	 * In the end, the output wires of both gates are filled with the
	 * values of the input.
	 * */
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
			
		//Sets the output of each gate according to the result from OCaml
		for (i = 0; i < VerYaoConfiguration.output.length(); i ++) {
			if (VerYaoConfiguration.output.charAt(i) == '1') {
				VerYaoConfiguration.out_wires1.get(i).setValue(true);
				VerYaoConfiguration.out_wires2.get(i).setValue(true);
			}
			else {
				VerYaoConfiguration.out_wires1.get(i).setValue(false);
				VerYaoConfiguration.out_wires2.get(i).setValue(false);
			}
		}
				
		//Cleans the auxiliary files created during the execution
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
