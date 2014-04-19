package net.xuset.smoothLife.world;

import java.awt.Color;
import java.awt.Graphics;
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
	
	public void draw(Graphics g, double scale) {
		Color mainColor = getColor();
		Color borderColor = wasAttacked ? mainColor.brighter() : mainColor.darker();
		double x = body.getX() * scale;
		double y = body.getY() * scale;
		double rb = body.getRadius() * scale; //radius border
		double rm = (body.getRadius() - 2) * scale; //radius main
		
		drawAngleView(g, scale, x, y);
		
		g.setColor(borderColor);
		g.fillOval(
				(int) (x - rb), (int) (y - rb),
				(int) (rb * 2), (int) (rb * 2));
		

		g.setColor(mainColor);
		g.fillOval(
				(int) (x - rm), (int) (y - rm),
				(int) (rm * 2), (int) (rm * 2));
		
		drawInnerCircle(g, scale, x, y);
		
		wasAttacked = false;
		
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
	
	private void drawAngleView(Graphics g, double scale, double x, double y) {
		double diam = (attackRange + body.getRadius()) * scale * 2;
		double viewAngle = Math.PI / 8;
		
		if (actionBuffer.contains(BlobActions.SPECIAL_ACTION) && !isPrey)
			g.setColor(Color.white);
		else
			g.setColor(Color.white);
		
		g.fillArc(
				(int) (x - diam/2), (int) (y - diam/2),
				(int) diam, (int) diam,
				(int) (Math.toDegrees(body.getAngle() - viewAngle)),
				(int) Math.toDegrees(2 * viewAngle));
	}
	
	private static final Color predatorInnerColor = new Color(100, 0, 0);
	private static final Color predatorInnerActiveColor = new Color(220, 0, 0);
	private void drawInnerCircle(Graphics g, double scale, double x, double y) {
		
		if (isPrey)
			g.setColor(getColor().brighter());
		else
			g.setColor(actionBuffer.contains(BlobActions.SPECIAL_ACTION) ?
					predatorInnerActiveColor : predatorInnerColor);

		double ri = body.getRadius() * 0.2 * scale;
		
		g.fillOval(
				(int) (x - ri), (int) (y - ri),
				(int) (ri * 2), (int) (ri * 2));
	}
}
