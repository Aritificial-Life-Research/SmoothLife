package net.xuset.smoothLife.world;

import java.util.List;

import net.xuset.smoothLife.nnetwork.Brain;

public final class BrainInterface {
	public static final int EXPECTED_INPUT = 8;
	public static final int EXPECTED_OUTPUT = BlobActions.values().length;
	
	private static final BlobActions[] actionList = BlobActions.values();
	
	public static void stimulateActions(Brain brain, List<BlobActions> actionBuffer,
			Blob main, Blob friend, Blob enemy) {
		
		if (brain.getInputCount() != EXPECTED_INPUT)
			throw new IllegalArgumentException("Brain input count must be " + EXPECTED_INPUT);
		if (brain.getOuputCount() != EXPECTED_OUTPUT)
			throw new IllegalArgumentException("Brain output count must be" + EXPECTED_OUTPUT);
		
		double[] input = new double[EXPECTED_INPUT];
		int offset = 0;
		input[offset++] = Math.cos(main.getBody().getAngle());
		input[offset++] = Math.sin(main.getBody().getAngle());
		
		addNeighborInput(main, enemy, input, offset);
		offset += 3;
		addNeighborInput(main, friend, input, offset);
		offset += 3;
		
		interpretOutput(brain.stimulate(input), actionBuffer);
	}
	
	private static void interpretOutput(double[] output, List<BlobActions> actionBuffer) {
		for (int i = 0; i < output.length; i++) {
			if (output[i] > 0.5) //TODO what to do here? hmmm...
				actionBuffer.add(actionList[i]);
		}
	}
	
	private static final int rangeDist = 1000;
	private static void addNeighborInput(Blob main, Blob target,
			double[] input, int offset) {
		
		if (target == null) {
			for (int i = 0; i < 3; i++)
				input[i] = 0;
			return;
		}
		
		double relativeX = main.getBody().getX() - target.getBody().getX();
		double relativeY = main.getBody().getY() - target.getBody().getY();
		double dist = Math.sqrt(relativeX * relativeX + relativeY * relativeY);
		
		double normX = relativeX / dist;
		double normY = relativeY / dist;
		double normDist = dist / rangeDist;
		
		input[offset++] = normX;
		input[offset++] = normY;
		input[offset++] = normDist;
	}
}
