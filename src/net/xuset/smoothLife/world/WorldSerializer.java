package net.xuset.smoothLife.world;

import java.util.Arrays;
import java.util.List;

import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.smoothLife.nnetwork.Chromosome;
import net.xuset.smoothLife.world.WorldFactory.SpecieInfo;
import net.xuset.smoothLife.world.WorldFactory.WorldInfo;

public class WorldSerializer{
	private static final String mainMsgName = "smoothLifeWorld";
	private static final String worldInfoName = "worldInfo";
	private static final String worldStateName = "worldState";
	
	private static final String xLocationAttribute = "x";
	private static final String yLocationAttribute = "y";
	private static final String angleAttribute = "angle";
	private static final String ageAttribute = "age";
	private static final String fitnessAttribute = "fitness";
	private static final String energyAttribute = "energy";
	private static final String geneAttribute = "genes";
	private static final String chromoName = "chromo";
	private static final String oldChromoName = "oldChromo";
	
	private static final String initBlobsAttribute = "initBlobCount";
	private static final String isPreyAttribute = "isPrey";
	private static final String specieIdAttribute = "specieId";
	private static final String nnLayoutAttribute = "neuronLayout";
	
	private static final String worldWidthAttribute = "worldWidth";
	private static final String worldHeightAttribute = "worldHeight";
	
	///////Begin World serialize////////
	
	public static MarkupMsg serializeWorld(World world) {
		MarkupMsg mainMsg = new MarkupMsg();
		mainMsg.setName(mainMsgName);
		
		MarkupMsg worldInfoMsg = serializeWorldInfo(world);
		MarkupMsg worldStateMsg = serializeWorldState(world);
		
		mainMsg.addNested(worldInfoMsg);
		mainMsg.addNested(worldStateMsg);
		
		return mainMsg;
	}
	
	private static MarkupMsg serializeWorldInfo(World world) {
		MarkupMsg worldInfoMsg = new MarkupMsg();
		worldInfoMsg.setName(worldInfoName);
		worldInfoMsg.setAttribute(worldWidthAttribute, world.getWidth());
		worldInfoMsg.setAttribute(worldHeightAttribute, world.getHeight());
		
		for (int i = 0; i < world.getSpeciesCount(); i++) {
			Specie specie = world.getSpecie(i);
			MarkupMsg specieInfoMsg = new MarkupMsg();
			specieInfoMsg.setAttribute(specieIdAttribute, specie.getSpecieId());
			specieInfoMsg.setAttribute(isPreyAttribute, specie.isPrey());
			specieInfoMsg.setAttribute(initBlobsAttribute, specie.getAllBlobs().size());
			specieInfoMsg.setAttribute(nnLayoutAttribute,
					Arrays.toString(specie.cloneNeuronLayout()));
			
			worldInfoMsg.addNested(specieInfoMsg);
		}
		
		return worldInfoMsg;
	}
	
	private static MarkupMsg serializeWorldState(World world) {
		MarkupMsg worldStateMsg = new MarkupMsg();
		worldStateMsg.setName(worldStateName);
		
		for (int i = 0; i < world.getSpeciesCount(); i++) {
			Specie specie = world.getSpecie(i);
			MarkupMsg specieMsg = new MarkupMsg();
			worldStateMsg.addNested(specieMsg);
			
			List<Blob> allBlobs = specie.getAllBlobs();
			for (Blob b : allBlobs)
				specieMsg.addNested(serializeBlob(b));
		}
		
		return worldStateMsg;
	}
	
	private static MarkupMsg serializeBlob(Blob b) {
		MarkupMsg blobMsg = new MarkupMsg();
		
		blobMsg.addNested(
				serializeChromosome(b.cloneChromosome(), chromoName));
		blobMsg.addNested(
				serializeChromosome(b.cloneOldChromosome(), oldChromoName));
		
		blobMsg.addAttribute(xLocationAttribute, b.getBody().getX());
		blobMsg.addAttribute(yLocationAttribute, b.getBody().getY());
		blobMsg.addAttribute(angleAttribute, b.getBody().getAngle());
		blobMsg.addAttribute(energyAttribute, b.getEnergy());
		blobMsg.addAttribute(ageAttribute, b.getAge());
		
		return blobMsg;
	}
	
	private static MarkupMsg serializeChromosome(Chromosome chromo, String msgName) {
		MarkupMsg chromoMsg = new MarkupMsg();
		chromoMsg.setName(msgName);
		
		chromoMsg.addAttribute(fitnessAttribute, chromo.getFitness());
		chromoMsg.addAttribute(geneAttribute,
				Arrays.toString(chromo.copyAllGenes()));
		
		return chromoMsg;
	}
	
	//////////End world serialize///////////
	////////////////////////////////////////
	//////////Begin world create////////////
	
	public static World createWorldFromMsg(MarkupMsg worldMsg) {
		if (!worldMsg.getName().equals(mainMsgName))
			throw new IllegalArgumentException("Supplied message is invalid");
		
		WorldInfo worldInfo = deserializeWorldInfo(worldMsg.getNested(worldInfoName));
		World world = new World(worldInfo);
		recreateWorldState(world, worldMsg.getNested(worldStateName));
		return world;
	}
	
	private static WorldInfo deserializeWorldInfo(MarkupMsg worldInfoMsg) {
		SpecieInfo[] specieInfos = new SpecieInfo[worldInfoMsg.getNestedMsgs().size()];
		for (int i = 0; i < worldInfoMsg.getNestedMsgs().size(); i++) {
			MarkupMsg infoMsg = worldInfoMsg.getNestedMsgs().get(i);
			
			long specieId = infoMsg.getAttribute(specieIdAttribute).getLong();
			boolean isPrey = infoMsg.getAttribute(isPreyAttribute).getBool();
			int initBlobCount = infoMsg.getAttribute(initBlobsAttribute).getInt();
			String strNeuronLayout = infoMsg.getAttribute(nnLayoutAttribute).getString();
			int[] neuronLayout = stringToIntArray(strNeuronLayout);
			
			specieInfos[i] = new SpecieInfo(isPrey, specieId, neuronLayout,
					initBlobCount);
		}
		
		int worldWidth = worldInfoMsg.getAttribute(worldWidthAttribute).getInt();
		int worldHeight = worldInfoMsg.getAttribute(worldHeightAttribute).getInt();
		return new WorldInfo(specieInfos, worldWidth, worldHeight);
	}
	
	private static void recreateWorldState(World world, MarkupMsg stateMsg) {
		for (int i = 0; i < world.getSpeciesCount(); i++) {
			MarkupMsg specieMsg = stateMsg.getNestedMsgs().get(i);
			Specie specie = world.getSpecie(i);
			
			if (specieMsg.getNestedMsgs().size() != specie.getBlobCount())
				throw new IllegalStateException("Blob counts do not match");
			
			for (int j = 0; j < specie.getBlobCount(); j++) {
				Blob blob = specie.getBlob(j);
				MarkupMsg blobMsg = specieMsg.getNestedMsgs().get(j);
				
				recreateBlob(blob, blobMsg);
			}
		}
	}
	
	private static void recreateBlob(Blob blob, MarkupMsg blobMsg) {
		blob.reset(
				blobMsg.getAttribute(xLocationAttribute).getDouble(), //x
				blobMsg.getAttribute(yLocationAttribute).getDouble(), //y
				createChromosome(blobMsg.getNested(oldChromoName)));  //oldChromo
		
		blob.reset(
				blob.getBody().getX(), blob.getBody().getY(),         //x, y
				blobMsg.getAttribute(angleAttribute).getDouble(),     //angle
				blobMsg.getAttribute(energyAttribute).getDouble(),    //energy
				blobMsg.getAttribute(ageAttribute).getInt(),          //age
				createChromosome(blobMsg.getNested(chromoName)));     //newChromo
	}
	
	private static Chromosome createChromosome(MarkupMsg chromoMsg) {
		String strGenes = chromoMsg.getAttribute(geneAttribute).getString();
		double[] genes = stringToDoubleArray(strGenes);
		double fitness = chromoMsg.getAttribute(fitnessAttribute).getDouble();
		
		Chromosome chromo = new Chromosome(genes);
		chromo.setFitness(fitness);
		return chromo;
	}
	
	///////////End world create//////////////
	
	private static int[] stringToIntArray(String str) {
		String[] strings = str.
				replace("[", "").
				replace("]", "").
				split(", ");
		
	    int result[] = new int[strings.length];
	    for (int i = 0; i < result.length; i++)
	      result[i] = Integer.parseInt(strings[i]);
	    
	    return result;
	}
	
	private static double[] stringToDoubleArray(String str) {
		String[] strings = str.
				replace("[", "").
				replace("]", "").
				split(", ");
		
	    double result[] = new double[strings.length];
	    for (int i = 0; i < result.length; i++)
	      result[i] = Double.parseDouble(strings[i]);
	    
	    return result;
	}
}
