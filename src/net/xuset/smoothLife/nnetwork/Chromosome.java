package net.xuset.smoothLife.nnetwork;

import java.util.Arrays;

public final class Chromosome implements Cloneable {
	private static final int brainOffset = 4;
	
	final double[] genes;
	private double fitness = 1.0;
	
	public Chromosome(int brainWeightCount, double randRange) {
		this(new double[brainOffset + brainWeightCount]);
		
		if (randRange < 0)
			throw new IllegalArgumentException("randRange must be greater than zero");
		
		for (int i = 0; i < genes.length; i++) {
			genes[i] = (Math.random() * randRange) - randRange / 2;
		}
	}
	
	public Chromosome(double[] genes) {
		if (genes.length <= brainOffset)
			throw new IllegalArgumentException(
					"Array length must be greater than " + brainOffset);
		
		this.genes = genes;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public void setFitness(double newFitness) {
		if (newFitness < 0)
			throw new IllegalArgumentException("newFitness must be greater than 0");
		fitness = newFitness;
	}
	
	public int getBrainWeightCount() {
		return genes.length - brainOffset;
	}
	
	public double[] copyBrainGenes() {
		return Arrays.copyOfRange(genes, brainOffset, genes.length);
	}
	
	public double[] copyAllGenes() {
		return Arrays.copyOf(genes, genes.length);
	}
	
	public int getRadius() {
		return normalizeRange(genes[0], 10) + 8;
	}
	
	public int getColorRed() {
		return normalizeRange(genes[1], 120);
	}
	
	public int getColorGreen() {
		return normalizeRange(genes[2], 200);
	}
	
	public int getColorBlue() {
		return normalizeRange(genes[3], 220) + 35;
	}
	
	@Override
	public Chromosome clone() {
		Chromosome chrm = new Chromosome(copyAllGenes());
		chrm.fitness = fitness;
		return chrm;
	}
	
	private int normalizeRange(double gene, int range) {
		return (int) Math.abs((gene * range) % range);
	}
}
