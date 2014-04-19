package net.xuset.smoothLife.world;

import net.xuset.smoothLife.nnetwork.Chromosome;
import net.xuset.smoothLife.nnetwork.GAlg;
import net.xuset.smoothLife.nnetwork.GenePool;
import net.xuset.smoothLife.nnetwork.GenePool.PoolItemKey;

public class BlobSpawner {
	private final GAlg gAlg;
	private final GenePool genePool;
	private final BlobFinder blobFinder;
	private final int worldWidth, worldHeight;
	private final int[] neuronLayout;
	
	BlobSpawner(GenePool genePool, BlobFinder blobFinder,
			int worldWidth, int worldHeight, int[] neuronLayout) {
		
		this.genePool = genePool;
		this.blobFinder = blobFinder;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.neuronLayout = neuronLayout;
		gAlg = new GAlg();
	}
	
	/*public Collection<Blob> createInitialBlobs(boolean isPrey, long specieId) {
		int size = genePool.getChromosomeCount();
		Collection<Blob> blobs = new ArrayList<Blob>(size);
		
		for (int i = 0; i < size; i++) {
			Chromosome chromo = genePool.getChromosome(i);
			Blob b = new Blob(neuronLayout, specieId, blobFinder, isPrey, chromo);
			SpawnPoint location = getSpawnLocation(b);
			b.getBody().setLocation(location.x, location.y);
			blobs.add(b);
		}
		
		return blobs;
	}*/
	
	public Blob create(boolean isPrey, long specieId) {
		Chromosome randChromo = new Chromosome(genePool.getBrainWeightCount(), 2);
		PoolItemKey key = genePool.createNewKey(randChromo);
		ChromosomeHolder chromoHolder = new ChromosomeHolder(genePool, key, randChromo);
		
		Blob b = new Blob(neuronLayout, specieId, blobFinder, isPrey, chromoHolder);
		resetLocation(b);
		return b;
	}
	
	public void respawn(Blob blob) {
		SpawnPoint location = getSpawnLocation(blob);
		Chromosome newChrmo = gAlg.createNewChromo(genePool);
		
		blob.reset(location.x, location.y, newChrmo);
	}
	
	public void resetLocation(Blob blob) {
		SpawnPoint location = getSpawnLocation(blob);
		blob.getBody().setLocation(location.x, location.y);
	}
	
	public double getSummedFitness() {
		return genePool.getSummedFitness();
	}
	
	public int getChromsomeCount() {
		return genePool.getChromosomeCount();
	}
	
	public GenePool cloneGenePool()  {
		return genePool.clone();
	}
	
	public int[] cloneNeuronLayout() {
		return neuronLayout.clone();
	}
	
	private SpawnPoint getSpawnLocation(Blob b) {
		final int maxIterations = 100;
		
		int iterations = 0;
		double x = 0, y = 0;
		do {
			x = Math.random() * worldWidth;
			y = Math.random() * worldHeight;
			b.getBody().setLocation(x, y);
		} while (blobFinder.getColliding(b.getBody()) != null &&
				iterations < maxIterations);
		
		if (iterations == maxIterations)
			throw new IllegalStateException("Could not find spawn for blob");
		
		return new SpawnPoint(x, y);
	}
	
	private static final class SpawnPoint {
		private final double x, y;
		
		SpawnPoint(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
}
