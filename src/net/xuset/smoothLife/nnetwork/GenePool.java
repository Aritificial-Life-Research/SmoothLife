package net.xuset.smoothLife.nnetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for storing Chromosomes. When a chromosome is stored it is
 * associated with key that can be used to retrieve the same chromosome or
 * swap out the chromosome for a new one. Each chromosome in the gene pool has
 * a key and all keys only work for one chromosome.
 * 
 * @author xuset
 * @since 1.0
 */
public final class GenePool implements Cloneable{

	/** The key for chromosome.  */
	public static class PoolItemKey {
		private PoolItemKey() { }
	}

	/** The object that holds the chromosome and key*/
	static class PoolItem {
		private final PoolItemKey key = new PoolItemKey();
		private Chromosome chromosome;

		PoolItem() { }
		PoolItem(Chromosome chromo) { chromosome = chromo; }
	}

	private final List<PoolItem> pool = new ArrayList<PoolItem>();
	private final int brainWeightCount;

	/**
	 * Instantiate a new and empty gene pool with the given brain weight count.
	 * All chromosomes in the gene pool must have the same brain weight count.
	 * 
	 * @param brainWeightCount the brain weight count of the chromosomes
	 */
	public GenePool(int brainWeightCount) {
		this.brainWeightCount = brainWeightCount;
	}

	/**
	 * Adds the given chromosome to the gene pool and returns a key for that
	 * chromosome. The key can be used to retrieved the chromosome or swap out
	 * the chromosome for a new one.
	 * 
	 * @param chromo the chromosome to add to the gene pool
	 * @return the key for the chromosome.
	 * @throws IllegalArgumentException if the given chromosome's brain weight
	 * 		count does not match the brain weight count of the gene pool.
	 */
	public PoolItemKey createNewKey(Chromosome chromo) {
		if (chromo.getBrainWeightCount() != brainWeightCount)
			throwCountMismatchException(chromo.getBrainWeightCount());

		PoolItem item = new PoolItem();
		item.chromosome = chromo;
		pool.add(item);
		return item.key;
	}

	/**
	 * Returns the chromosome that is associated with the given key.
	 * 
	 * @param key the key of the chromosome to return
	 * @return the chromosome that is matched with the key
	 * @throws IllegalArgumentException if no chromosome matches the given key
	 */
	public Chromosome getChromosome(PoolItemKey key) {
		return getPoolItem(key).chromosome;
	}

	/**
	 * Swaps the chromosome associated with the key for the new given chromsome.
	 * After the swap, the new chromosome will be associated with the same key.
	 * 
	 * @param key the key of the chromosome to swap
	 * @param newChromosome the new chromosome for the key
	 */
	public void setChromosome(PoolItemKey key, Chromosome newChromosome) {
		if (newChromosome == null)
			throw new NullPointerException("Chromosome cant be null");

		getPoolItem(key).chromosome = newChromosome;
	}

	/**
	 * Calculates the summed fitness of the entire gene pool.
	 * 
	 * @return the summed fitness of the gene pool
	 */
	public double getSummedFitness() {

		double total = 0;
		for(int i = 0; i < pool.size(); i++)
			total += pool.get(i).chromosome.getFitness();

		return total;
	}

	/**
	 * Gets the brain weight count of the gene pool and it's chromosomes.
	 * 
	 * @return the brain weight count
	 */
	public int getBrainWeightCount() {
		return brainWeightCount;
	}

	/**
	 * Gets the amount of chromosomes that are being stored in the gene pool.
	 * 
	 * @return the chromosome count of the gene pool
	 */
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

	/**
	 * Gets the chromosome at the specified index.
	 * 
	 * @param index the index of the chromosome to get. The index should be
	 * >= 0 and < getChromosomeCount().
	 * @return the chromosome at the specified index
	 */
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
