package net.xuset.smoothLife.world;


/**
 * Used to represent the physical body.
 * The Body object holds information like the location, size, and direction
 * of the Blob.
 * 
 * @author xuset
 * @since 1.0
 */
public final class Body {
	private double x, y, radius, angle, moveCoefficient = 1.0;

	/**
	 * Instantiate an empty Body.
	 * The reset method needs to be called to set the initial values.
	 */
	Body() {

	}

	/**
	 * Gets the location of the body in the x dimension.
	 * @return the x location value of the body
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the location of the body in the y dimension.
	 * @return the y location value of the body
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the radius of the body.
	 * @return the value of the radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Returns the angle that the body is facing.
	 * @return the angle in radians.
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * Calculates the distance from the body's current location to the
	 * given (x,y) location.
	 * 
	 * @param x the x value of the location to find the distance to
	 * @param y the y value of the location to find the distance to
	 * @return the distance between the body and (x,y) location
	 */
	public double getDistanceFrom(double x, double y) {
		return Math.sqrt(getSqrDistanceFrom(x, y));
	}

	/**
	 * Calculates the squared distance from the body's current location to the
	 * given (x,y) location. This method is similar to getDistanceFrom but
	 * instead the returned values are squared.
	 * 
	 * @param x the x value of the location to find the squared distance to
	 * @param y the y value of the location to find the squared distance to
	 * @return the squared distance between the body and (x,y) location
	 */
	public double getSqrDistanceFrom(double x, double y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return dx * dx + dy * dy;
	}

	/**
	 * Indicates if the the given body's location is within the given range
	 * of the current body. The bodies radii are taken into account wit this
	 * method.
	 * 
	 * @param body the body to compare distance with
	 * @param range the minimum amount of distance to be considered within range
	 * @return true if the the distance between the current body and the given
	 * 		body is less than or equal to the given range.
	 */
	public boolean isWithinRange(Body body, double range) {
		double sqrDist = getSqrDistanceFrom(body.x, body.y);
		double radii = radius + body.radius;
		return (sqrDist - (radii * radii + range * range) <= 0.0);
	}

	/**
	 * Indicates if the given body is touching the current body.
	 * 
	 * @param check the body to check
	 * @return true if the bodies are colliding, false otherwise.
	 */
	public boolean isColliding(Body check) {
		return isWithinRange(check, 0.0);
	}

	/**
	 * Adds the given angle to the body's angle.
	 * 
	 * @param deltaAngle the amount to change the current angle by in radians
	 */
	void adjustAngle(double deltaAngle) {
		angle += deltaAngle;
	}

	/**
	 * Sets the body's angle to the given angle
	 * 
	 * @param newAngle the new angle of the body in radians
	 */
	void setAngle(double newAngle) {
		angle = newAngle;
	}

	/**
	 * Sets the body's location to the given location.
	 * 
	 * @param newX the new x value of the body
	 * @param newY the new y value of the body
	 */
	void setLocation(double newX, double newY) {
		x = newX;
		y = newY;
	}

	/**
	 * Moves the body forward the given distance in the direction of the body's
	 * current angle. If the body collides with another body, the body is moved
	 * back.
	 * 
	 * @param distance the distance to move forward
	 * @param blobFinder the object used to find nearby bodies and test for
	 * 		collisions.
	 */
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

	/**
	 * Sets the move coefficients for the body.
	 * Move coefficients are used by the moveForward function.
	 * @param newCoefficient the new move coefficient of the body
	 */
	void setMoveCoefficient(double newCoefficient) {
		moveCoefficient = newCoefficient;
	}

	/**
	 * Resets the body to the given location, size, and direction.
	 * 
	 * @param x the new x value of the body
	 * @param y the new y value of the body
	 * @param radius the new radius of the body
	 * @param angle the new direction of of the body in radians
	 */
	void reset(double x, double y, double radius, double angle) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.angle = angle;
	}
}
