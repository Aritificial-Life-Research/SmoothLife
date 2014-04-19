package net.xuset.smoothLife.nnetwork;

import java.util.ArrayList;
import java.util.List;

public final class GenePool implements Cloneable{
	
	public static class PoolItemKey { 
		private PoolItemKey() { }
	}
	
	static class PoolItem {
		private final PoolItemKey key = new PoolItemKey();
		private Chromosome chromosome;
		
		PoolItem() { }
		PoolItem(Chromosome chromo) { chromosome = chromo; }
	}
	
	private final List<PoolItem> pool = new ArrayList<PoolItem>();
	private final int brainWeightCount;
	
	public GenePool(int brainWeightCount) {
		this.brainWeightCount = brainWeightCount;
	}
	
	public PoolItemKey createNewKey(Chromosome chromo) {
		if (chromo.getBrainWeightCount() != brainWeightCount)
			throwCountMismatchException(chromo.getBrainWeightCount());
			
		
		PoolItem item = new PoolItem();
		item.chromosome = chromo;
		pool.add(item);
		return item.key;
	}
	
	public Chromosome getChromosome(PoolItemKey key) {
		return getPoolItem(key).chromosome;
	}
	
	public void setChromosome(PoolItemKey key, Chromosome newChromosome) {
		if (newChromosome == null)
			throw new NullPointerException("Chromosome cant be null");
		
		getPoolItem(key).chromosome = newChromosome;
	}
	
	public double getSummedFitness() {
		
		double total = 0;
		for(int i = 0; i < pool.size(); i++)
			total += pool.get(i).chromosome.getFitness();
		
		return total;
	}
	
	public int getBrainWeightCount() {
		return brainWeightCount;
	}
	
	public int getChromosomeCount() {
		return pool.size();
	}
	
	@Override
	public GenePool clone() {
		GenePool cloned = new GenePool(brainWeightCount);
		
		for (PoolItem pi : pool) {
			PoolItem newPi = new PoolItem();
			newPi.chromosome = pi.chromosome.clone();
			cloned.pool.add(newPi);
		}
		
		return cloned;
	}
	
	Chromosome getChromosome(int index) {
		return pool.get(index).chromosome;
	}
	
	private PoolItem getPoolItem(PoolItemKey key) {
		for (int i = 0; i < pool.size(); i++) {
			if(pool.get(i).key == key)
				return pool.get(i);
		}
		
		throw new IllegalArgumentException("Key argument must be contained in pool");
	}
	
	private void throwCountMismatchException(int actual) {
		throw new IllegalArgumentException(
				"Chromosome weight count must match the the specified count. Was "
				+ actual + ", expected " + brainWeightCount + ".");
	}
}
