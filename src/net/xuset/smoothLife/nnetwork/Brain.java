package net.xuset.smoothLife.nnetwork;

public final class Brain {
	private final int inputCount;
	private final int outputCount;
	private final int weightCount;
	
	private final Layer[] layers;
	
	public Brain(int neuronLayout[]) {
		
		/*
		 * make sure there is at least two layers.
		 * one for an input layer, the other for an output layer
		 */
		if (neuronLayout.length < 2)
			throw new IllegalArgumentException("The arrays must have a length greater " +
					" than or equal to 2");
		
		inputCount = neuronLayout[0];
		outputCount = neuronLayout[neuronLayout.length - 1];
		layers = new Layer[neuronLayout.length - 1];
		int totalWeights = 0;
		for (int i = 0; i < layers.length; i++) {
			
			//check for valid neuron count
			if (neuronLayout[i] < 1 || neuronLayout[i + 1] < 1)
				throw new IllegalArgumentException("Array values must be greater than 0");
			
			layers[i] = new Layer(neuronLayout[i], neuronLayout[i+1]);
			
			totalWeights += layers[i].weightCount;
		}
		weightCount = totalWeights;
	}
	
	public void putWeights(double weights[]) {
		
		//make sure the supplied weight count is equal to the brain's weight count
		if (weights.length != weightCount)
			throw new IllegalArgumentException("weight counts must equal");
		
		int offset = 0;
		for (int i = 0; i < layers.length; i++) {
			offset = layers[i].putWeights(offset, weights);
		}
	}
	
	public double[] getWeights() {
		double[] totalWeights = new double[weightCount];
		int totalCount = 0;
		for (int i = 0; i < layers.length; i++) {
			Neuron neurons[] = layers[i].neurons;
			for (int j = 0; j < neurons.length; j++) {
				double weights[] = neurons[j].weights;
				for (int k = 0; k < weights.length; k++) {
					totalWeights[totalCount++] = weights[k];
				}
			}
		}
		return totalWeights;
	}
	
	public double[] stimulate(double input[]) {
		
		//make sure the input array equals the input count of the brain
		if (input.length != inputCount)
			throw new IllegalArgumentException("Input count does not equal");
		
		double layerIn[] = input;
		for (int i = 0; i < layers.length; i++) {
			Layer layer = layers[i];
			layerIn = layer.stimulate(layerIn);
		}
		return layerIn;
	}
	
	public int getInputCount() { return inputCount; }
	
	public int getOuputCount() { return outputCount; }
	
	public int getWeightCount() { return weightCount; }
	
	public static int getWeightCount(int[] neuronLayout) {
		int count = 0;
		for (int i = 0; i < neuronLayout.length - 1; i++) {
			count += (neuronLayout[i] + 1) * neuronLayout[i + 1];
		}
		return count;
	}

}
