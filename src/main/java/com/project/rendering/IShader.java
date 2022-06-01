package com.project.rendering;

import org.joml.Matrix4f;

public interface IShader {
	// methods for setting uniforms of different types
	public void setUniform(String name, int value);
	public void setUniform(String name, float value);
	public void setUniform(String name, Matrix4f value);
	
	// method for binding the shader to be the current one to render
	public void bind();
}
