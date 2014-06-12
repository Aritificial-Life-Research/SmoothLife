package net.xuset.smoothLife.world;

import net.xuset.smoothLife.nnetwork.Chromosome;
import net.xuset.smoothLife.nnetwork.GAlg;
import net.xuset.smoothLife.nnetwork.GenePool;
import net.xuset.smoothLife.nnetwork.GenePool.PoolItemKey;

/**
 * Used to create and spawn blobs in the world.
 * 
 * @author xuset
 * @since 1.0
 */
public class BlobSpawner {

	private final GAlg gAlg;
	private final GenePool genePool;
	private final BlobFinder blobFinder;
	private final int worldWidth, worldHeight;
	private final int[] neuronLayout;

	/**
	 * Instantiate the blob spawner
	 * 
	 * @param genePool the object used to store chromosomes
	 * @param blobFinder the object used to find blobs in the world
	 * @param worldWidth the width of the world
	 * @param worldHeight the height of the world
	 * @param neuronLayout the brains neural layout for the blobs.
	 */
	BlobSpawner(GenePool genePool, BlobFinder blobFinder,
			int worldWidth, int worldHeight, int[] neuronLayout) {

		this.genePool = genePool;
		this.blobFinder = blobFinder;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.neuronLayout = neuronLayout;
		gAlg = new GAlg();
	}

	/**
	 * Create a new blob. The blob's chromosomes are added to the gene pool,
	 * and the blob's location is set to a non colliding location.
	 * 
	 * @param isPrey determines if the blob should be prey or a predator
	 * @param specieId the specie id of specie the blob belongs to
	 * @return the newly created blob
	 */
	public Blob create(boolean isPrey, long specieId) {
		Chromosome randChromo = new Chromosome(genePool.getBrainWeightCount(), 2);
		PoolItemKey key = genePool.createNewKey(randChromo);
		ChromosomeHolder chromoHolder = new ChromosomeHolder(genePool, key, randChromo);

		Blob b = new Blob(neuronLayout, specieId, blobFinder, isPrey, chromoHolder);
		resetLocation(b);
		return b;
	}

	/**
	 * Calls reset on the blob with a non-colliding location with a new
	 * chromosome.
	 * 
	 * @param blob the blob to reset
	 */
	public void respawn(Blob blob) {
		SpawnPoint location = getSpawnLocation(blob);
		Chromosome newChrmo = gAlg.createNewChromo(genePool);

		blob.reset(location.x, location.y, newChrmo);
	}

	/**
	 * Sets the location of the blob's body to a new non-colliding location.
	 * 
	 * @param blob
	 */
	public void resetLocation(Blob blob) {
		SpawnPoint location = getSpawnLocation(blob);
		blob.getBody().setLocation(location.x, location.y);
	}

	/**
	 * Gets the summed fitness of the genepool.
	 * 
	 * @return the summed fitness of the genepool
	 */
	public double getSummedFitness() {
		return genePool.getSummedFitness();
	}

	/**
	 * Gets the chromosome count of the genepool.
	 * 
	 * @return the chromosome count of the genepool
	 */
	public int getChromsomeCount() {
		return genePool.getChromosomeCount();
	}

	/**
	 * Clones and returns the genepool.
	 * 
	 * @return a cloned genepool
	 */
	public GenePool cloneGenePool()  {
		return genePool.clone();
	}

	/**
	 * Clones and returns the neural layout of the blob's brains.
	 * @return a copy of the brains' neuron layout
	 */
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
