package net.xuset.smoothLife.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.xuset.smoothLife.nnetwork.Brain;
import net.xuset.smoothLife.nnetwork.Chromosome;

public class Blob {
	private static final double attackEnergyDelta = -3.0, attackPayoff = -1.0;
	private static final double attackRange = 7;
	private static final double groupRange = 10;
	private static final double moveForwardDelta = 0.3, turnDelta = 0.1;
	private static final double costToLive = -1.0;
	private static final double initEnergy = 1000.0;
	
	private final List<BlobActions> actionBuffer =
			new ArrayList<BlobActions>(BlobActions.values().length);
	private final Body body = new Body();
	private final Brain brain;
	private final long specieId;
	private final BlobFinder blobFinder;
	private final boolean isPrey;
	private final ChromosomeHolder chromoHolder;
		
	private int age;
	private double energy;
	private boolean wasAttacked = false;
	
	Blob(int[] neuronLayout, long specieId, BlobFinder blobFinder, boolean isPrey,
			ChromosomeHolder chromosomeHolder) {
		
		brain = new Brain(neuronLayout);
		this.specieId = specieId;
		this.blobFinder = blobFinder;
		this.isPrey = isPrey;
		this.chromoHolder = chromosomeHolder;
		
		reset(0, 0, chromoHolder.getChromosome().clone());
		body.setMoveCoefficient(isPrey ? 0.5 : 2.0);
	}
	
	public Body getBody() {
		return body;
	}
	
	public double getEnergy() {
		return energy;
	}
	
	public int getAge() {
		return age;
	}
	
	public Color getColor() {
		int r = chromoHolder.getChromosome().getColorRed();
		int g = chromoHolder.getChromosome().getColorGreen();
		int b = chromoHolder.getChromosome().getColorBlue();
		return new Color(r, g, b);
	}
	
	public boolean wasAttacked() {
		return wasAttacked;
	}
	
	public boolean isPrey() {
		return isPrey;
	}
	
	public BlobActions[] getActions() {
		BlobActions[] buff = new BlobActions[actionBuffer.size()];
		return actionBuffer.toArray(buff);
	}
	
	long getSpecieId() {
		return specieId;
	}

	void reset(double x, double y, Chromosome newChromo) {
		reset(x, y,
				Math.random() * 2 * Math.PI,               //angle
				initEnergy, 1,                             //energy, age
				newChromo);  //newChromo, oldChromo
	}
	
	void reset(double x, double y, double angle, double newEnergy, int newAge,
			Chromosome newChromo) {

		chromoHolder.getChromosome().setFitness(age);
		
		this.age = newAge;
		this.energy = newEnergy;
		
		chromoHolder.replaceChromosome(newChromo);
		
		actionBuffer.clear();
		brain.putWeights(newChromo.copyBrainGenes());
		body.reset(x, y, newChromo.getRadius(), angle);
	}
	
	void preupdate() {
		wasAttacked = false;
	}
	
	void update() {
		age++;
		energy += costToLive;
		
		performActions();
	}
	
	void performActions() {
		actionBuffer.clear();
		
		Blob friend = blobFinder.getClosestSimilar(this);
		Blob enemy = blobFinder.getClosestUnSimilar(this);
		BrainInterface.stimulateActions(brain, actionBuffer, this, friend, enemy);
		
		for (int i = 0; i < actionBuffer.size(); i++) {
			switch(actionBuffer.get(i)) {
			case TURN_LEFT:
				actionTurn(turnDelta);
				break;
			case TURN_RIGHT:
				actionTurn(-turnDelta);
				break;
			case MOVE_FORWARD:
				actionMove();
				break;
			case SPECIAL_ACTION:
				actionSpecial();
				break;
			}
		}
	}
	
	Chromosome cloneChromosome() {
		return chromoHolder.getChromosome().clone();
	}
	
	Chromosome cloneOldChromosome() {
		return chromoHolder.cloneChromoInGenePool();
	}
	
	private void actionTurn(double deltaAngle) {
		body.adjustAngle(deltaAngle);
	}
	
	private void actionMove() {
		body.moveForward(moveForwardDelta, blobFinder);
	}
	
	private void actionSpecial() {
		if (isPrey) {
			actionGroupHelp();
		} else {
			actionAttack();
		}
	}
	
	private void actionAttack() {
		Blob prey = blobFinder.getClosestUnSimilar(this);
		if (prey != null && body.isWithinRange(prey.body, attackRange)) {
			prey.wasAttacked = true;
			prey.energy += attackEnergyDelta;
			energy += attackEnergyDelta * attackPayoff;
		} else {
			//energy--;
		}
	}
	
	private void actionGroupHelp() {
		Blob friendly = blobFinder.getClosestSimilar(this);
		if (friendly != null && body.isWithinRange(friendly.body, groupRange))
			energy += -costToLive;
	}
}
