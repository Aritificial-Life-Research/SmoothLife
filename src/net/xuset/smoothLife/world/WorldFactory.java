package net.xuset.smoothLife.world;

import net.xuset.objectIO.markupMsg.MarkupMsg;

public class WorldFactory {
	private static final int defaultWidth = 800, defaultHeight = 600;
	private static final int defaultSpeciesCount = 2;
	private static final int defaultInitBlobCount = 20;
	private static final int[] defaultNeuronLayout =
			{ BrainInterface.EXPECTED_INPUT, 20, 20, BrainInterface.EXPECTED_OUTPUT };

	
	public WorldFactory() {
		
	}
	
	public World createNewWorld(WorldInfo worldInfo) {
		return new World(worldInfo);
	}
	
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
	
	public World createNewWorld(MarkupMsg worldMsg) {
		return WorldSerializer.createWorldFromMsg(worldMsg);
	}
	
	public static class WorldInfo {
		final int worldWidth, worldHeight;
		final SpecieInfo[] speciesInfos;
		
		public WorldInfo(SpecieInfo[] specieInfos, int worldWidth, int worldHeight) {
			this.speciesInfos = specieInfos;
			this.worldWidth = worldWidth;
			this.worldHeight = worldHeight;
		}
	}
	
	public static class SpecieInfo {
		final int[] neuronLayout;
		final boolean isPrey;
		final long specieId;
		final int initBlobCount;
		
		public SpecieInfo(boolean isPrey, long specieId,
				int[] neuronLayout, int initBlobCount) {
			
			this.isPrey = isPrey;
			this.specieId = specieId;
			this.neuronLayout = neuronLayout;
			this.initBlobCount = initBlobCount;
		}
	}
}
