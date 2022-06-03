package project.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

// class that implements a virtual camera
public class Camera implements ICamera {
	// vector representing the camera's position
	private Vector3f position;
	// matrix representing the camera's projection
	private Matrix4f projection;
	
	// Constructor
	public Camera(int width, int height) {
		// set the camera position to 0
		this.position = new Vector3f(0, 0, 0);
		// set the projection to be an orthogonal projection based on the screen width and height
		this.projection = new Matrix4f().setOrtho2D(-width / 2, width / 2, -height / 2, height / 2);
	}
	
	// getters and setters for the camera position
	public void setPosition(float x, float y) {
		this.position = new Vector3f(x, y, 0);
	}
	public float getX() {
		return(this.position.x);
	}
	public float getY() {
		return(this.position.y);
	}
	
	// getters and setters for the camera projection matrix
	public Matrix4f getProjection() {
		// new identity matrix
		Matrix4f target = new Matrix4f();
		// calculate a translation matrix that matches the position of the camera
		Matrix4f pos = new Matrix4f().setTranslation(this.position);
		// multiply the projection matrix with the translation matrix and store the value in the target matrix
		this.projection.mul(pos, target);
		// return the target matrix
		return(target);
	}
	public void setProjection(int width, int height) {
		// create a new projection matrix with the new width and height
		this.projection = new Matrix4f().setOrtho2D(-width / 2, width / 2, -height / 2, height / 2);
	}
}
