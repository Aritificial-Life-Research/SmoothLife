package net.xuset.smoothLife.world;

import net.xuset.objectIO.markupMsg.MarkupMsg;

/**
 * Used to create world objects.
 * A default world can be created or a specialized world can.
 * 
 * @author xuset
 * @since 1.0
 * @see World
 */
public class WorldFactory {

	private static final int defaultWidth = 800, defaultHeight = 600;
	private static final int defaultSpeciesCount = 2;
	private static final int defaultInitBlobCount = 20;
	private static final int[] defaultNeuronLayout =
		{ BrainInterface.EXPECTED_INPUT, 20, 20, BrainInterface.EXPECTED_OUTPUT };


	/**
	 * Create a new world factory.
	 */
	public WorldFactory() {

	}

	/**
	 * Creates a new world based on the world info object.
	 * 
	 * @param worldInfo the object used to determine the worlds properties
	 * @return the newly created world
	 */
	public World createNewWorld(WorldInfo worldInfo) {
		return new World(worldInfo);
	}

	/**
	 * Creates a new world based on the default properties.
	 * 
	 * @return the newly created world
	 */
	public World createNewWorld() {
		SpecieInfo[] speciesInfo = new SpecieInfo[defaultSpeciesCount];
		for (int i = 0; i < speciesInfo.length; i++) {
			long specieId = (long) i;
			boolean isPrey = i % 2 == 0;
			speciesInfo[i] = new SpecieInfo(isPrey, specieId,
					defaultNeuronLayout, defaultInitBlobCount);
		}

		WorldInfo worldInfo = new WorldInfo(speciesInfo,
				defaultWidth, defaultHeight);
		return createNewWorld(worldInfo);
	}

	/**
	 * Creates a new world based on the given markupmsg. The given
	 * markupmsg should be a serialized world created with the
	 * WorldSerializer class.
	 * 
	 * @param worldMsg the serialized world message used to create the world
	 * @return the newly created world
	 * @see WorldSerializer
	 */
	public World createNewWorld(MarkupMsg worldMsg) {
		return WorldSerializer.createWorldFromMsg(worldMsg);
	}


	/**
	 * Class used to define properties of a world so it can be created.
	 */
	public static class WorldInfo {

		/**
		 * The width of the world
		 */
		final int worldWidth;

		/**
		 * The height of the world
		 */
		final int worldHeight;

		/**
		 * The array of species info. Each species info object is used to
		 * create a new species in the world based on the info in the object.
		 */
		final SpecieInfo[] speciesInfos;

		/**
		 * Instantiate a new instance.
		 * @param specieInfos the species info used to create the species
		 * @param worldWidth the width of the world
		 * @param worldHeight the height of the world
		 */
		public WorldInfo(SpecieInfo[] specieInfos, int worldWidth, int worldHeight) {
			this.speciesInfos = specieInfos;
			this.worldWidth = worldWidth;
			this.worldHeight = worldHeight;
		}
	}


	/**
	 * Used to define the properties of a species to create. Each SpecieInfo
	 * object is used to create a new species.
	 */
	public static class SpecieInfo {

		/**
		 * The layout of neurons in the blob's brain.
		 */
		final int[] neuronLayout;

		/**
		 * Indicates if this species will be prey or predators.
		 */
		final boolean isPrey;

		/**
		 * The species id of the species.
		 */
		final long specieId;

		/**
		 * The inital amount of blobs to spawn.
		 */
		final int initBlobCount;

		/**
		 * Instantiate a new species info object.
		 * 
		 * @param isPrey indicates if the species should be prey or predators
		 * @param specieId the species id of the species
		 * @param neuronLayout the layout of neurons in this species' blobs
		 * @param initBlobCount the initial amount of blobs to spawn
		 */
		public SpecieInfo(boolean isPrey, long specieId,
				int[] neuronLayout, int initBlobCount) {

			this.isPrey = isPrey;
			this.specieId = specieId;
			this.neuronLayout = neuronLayout;
			this.initBlobCount = initBlobCount;
		}
	}
}
