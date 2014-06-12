package net.xuset.smoothLife.world;

import java.util.ArrayList;
import java.util.List;

import net.xuset.smoothLife.nnetwork.GenePool;


/**
 * The species holds all the blobs that are apart of that species.
 * This class is responsible for updating the blobs, removing the
 * dead blobs, and respawning blobs.
 * 
 * @author xuset
 * @since 1.0
 */
public class Specie {

	private static final int ticksPerSpawn = 50;

	private final List<Blob> deadBlobs = new ArrayList<Blob>();
	private final List<Blob> liveBlobs = new ArrayList<Blob>();
	private final BlobSpawner blobSpawner;
	private final long specieId;
	private final boolean isPrey;

	private int lastSpawnTick = 0;

	/**
	 * Instantiate a new species.
	 * 
	 * @param specieId the id of the species. This should be unique to all other
	 * 		species
	 * @param blobSpawner the spawner object used to create blobs
	 * @param isPrey indicates if this species should be prey or predators
	 * @param initBlobCount the initial amount of blobs to spawn
	 */
	Specie(long specieId, BlobSpawner blobSpawner, boolean isPrey, int initBlobCount) {
		this.specieId = specieId;
		this.blobSpawner = blobSpawner;
		this.isPrey = isPrey;

		for (int i = 0; i < initBlobCount; i++)
			liveBlobs.add(blobSpawner.create(isPrey, specieId));
	}

	/**
	 * Indicates if this species is prey or predators.
	 * 
	 * @return true if this species is prey, false if this species is predators
	 */
	public boolean isPrey() {
		return isPrey;
	}

	/**
	 * Returns the amount of blobs that are alive.
	 * 
	 * @return the count of all the blobs that are alive
	 */
	public int getBlobCount() {
		return liveBlobs.size();
	}

	/**
	 * Returns the blob at the specified index.
	 * 
	 * @param index the index of the blob to return. The index should be >= 0
	 * and should be < getBlobCount().
	 * @return the Blob at the specified index
	 */
	public Blob getBlob(int index) {
		return liveBlobs.get(index);
	}

	/**
	 * Return the summed fitness of all the blobs in the species.
	 * @return the summed fitness of the species
	 */
	public double getSummedFitness() {
		return blobSpawner.getSummedFitness();
	}

	/**
	 * Return a new list of all live and dead blobs.
	 * 
	 * @return a list of all live and dead blobs.
	 */
	List<Blob> getAllBlobs() {
		List<Blob> list = new ArrayList<Blob>();
		list.addAll(liveBlobs);
		list.addAll(deadBlobs);
		return list;
	}

	/**
	 * Create and return a clone of the species' genepool.
	 * 
	 * @return the cloned genepool
	 */
	GenePool cloneGenePool() {
		return blobSpawner.cloneGenePool();
	}

	/**
	 * Create and return a clone of the nuron layout used by the blob's brain.
	 * 
	 * @return a new array of the nueron layout
	 */
	int[] cloneNeuronLayout() {
		return blobSpawner.cloneNeuronLayout();
	}

	/**
	 * Get this species' specie id.
	 * 
	 * @return the specie id of this species
	 */
	long getSpecieId() {
		return specieId;
	}

	/**
	 * Called all all species before updateBlobs is called.
	 * This method removes any dead blobs and spawns any new blobs.
	 * The preupdate method is called on all the live blobs.
	 */
	void preupdateBlobs() {
		for (int i = 0; i < liveBlobs.size(); i++) {
			Blob b = liveBlobs.get(i);
			b.preupdate();

			if (b.getEnergy() <= 0.0) {
				deadBlobs.add(b);
				liveBlobs.remove(i);
				i--;
			}
		}

		if (lastSpawnTick == ticksPerSpawn) {
			spawnBlob();
			lastSpawnTick = 0;
		}

		lastSpawnTick++;
	}

	/**
	 * Calls the update method on the all the live blobs.
	 */
	void updateBlobs() {
		for (int i = 0; i < liveBlobs.size(); i++) {
			Blob b = liveBlobs.get(i);
			b.update();
		}
	}

	private void spawnBlob() {
		if (deadBlobs.isEmpty())
			return;

		Blob b = deadBlobs.remove(0);
		blobSpawner.respawn(b);
		liveBlobs.add(b);
	}
}
