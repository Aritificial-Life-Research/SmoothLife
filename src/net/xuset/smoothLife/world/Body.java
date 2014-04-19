package net.xuset.smoothLife.world;


public final class Body {
	private double x, y, radius, angle, moveCoefficient = 1.0;
	
	Body() {
		
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public double getDistanceFrom(double x, double y) {
		return Math.sqrt(getSqrDistanceFrom(x, y));
	}
	
	public double getSqrDistanceFrom(double x, double y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return dx * dx + dy * dy;
	}
	
	public boolean isWithinRange(Body body, double range) {
		double sqrDist = getSqrDistanceFrom(body.x, body.y);
		double radii = radius + body.radius;
		return (sqrDist - (radii * radii + range * range) <= 0.0);
	}
	
	public boolean isColliding(Body check) {
		return isWithinRange(check, 0.0);
	}
	
	void adjustAngle(double deltaAngle) {
		angle += deltaAngle;
	}
	
	void setAngle(double newAngle) {
		angle = newAngle;
	}
	
	void setLocation(double newX, double newY) {
		x = newX;
		y = newY;
	}
	
	void moveForward(double distance, BlobFinder blobFinder) {
		double dx = Math.cos(angle) * distance * moveCoefficient;
		double dy = -Math.sin(angle) * distance * moveCoefficient;
		
		x += dx;
		if (blobFinder.getColliding(this) != null)
			x -= dx;
		
		y += dy;
		if (blobFinder.getColliding(this) != null)
			y -= dy;
	}
	
	void setMoveCoefficient(double newCoefficient) {
		moveCoefficient = newCoefficient;
	}
	
	void reset(double x, double y, double radius, double angle) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.angle = angle;
	}
}
