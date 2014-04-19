package net.xuset.smoothLife.nnetwork;

final class Layer {
	final int inputs;
	final int outputs;
	final int weightCount;
	
	final Neuron neurons[];
	
	Layer(int inputs, int outputs, boolean sigmoid) {
		this.inputs = inputs;
		this.outputs = outputs;
		neurons = new Neuron[outputs];
		int totalWeights = 0;
		for (int i = 0; i < neurons.length; i++) {
			neurons[i] = new Neuron(inputs, sigmoid);
			totalWeights += neurons[i].weights.length;
		}
		weightCount = totalWeights;
	}
	
	Layer(int inputs, int outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
		neurons = new Neuron[outputs];
		int totalWeights = 0;
		for (int i = 0; i < neurons.length; i++) {
			neurons[i] = new Neuron(inputs, false);
			totalWeights += neurons[i].weights.length;
		}
		weightCount = totalWeights;
	}
	
	double[] stimulate(double input[]) {
		double output[] = new double[neurons.length];
		for (int i = 0; i < neurons.length; i++) {
			Neuron neuron = neurons[i];
			output[i] = neuron.stimulate(input);
		}
		return output;
	}
	
	int putWeights(int offset, double[] weights) {
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < neurons[i].weights.length; j++) {
				neurons[i].weights[j] = weights[offset++];
			}
		}
		return offset;
	}
}
