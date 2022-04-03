package com.test;

import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Shader {
	private int program;
	private int vertID;
	private int fragID;
	
	public Shader(String filename) {
		this.program = glCreateProgram();
		
		this.vertID = glCreateShader(GL_VERTEX_SHADER);
		
		glShaderSource(this.vertID, readFile(filename + ".vert"));
		
		glCompileShader(this.vertID);
		
		if (glGetShaderi(this.vertID, GL_COMPILE_STATUS) != 1) {
			System.err.println(glGetShaderInfoLog(this.vertID));
			System.exit(1);
		}
		
		
		this.fragID = glCreateShader(GL_FRAGMENT_SHADER);
		
		glShaderSource(this.fragID, readFile(filename + ".frag"));
		
		glCompileShader(this.fragID);
		
		if (glGetShaderi(this.fragID, GL_COMPILE_STATUS) != 1) {
			System.err.println(glGetShaderInfoLog(this.fragID));
			System.exit(1);
		}
		
		
		
		glAttachShader(this.program, this.vertID);
		glAttachShader(this.program, this.fragID);
		
		
		glBindAttribLocation(this.program, 0, "vertices");
		glBindAttribLocation(this.program, 1, "textures");
		
		glLinkProgram(this.program);
		
		if (glGetProgrami(this.program, GL_LINK_STATUS) != 1) {
			System.err.println(glGetProgramInfoLog(this.program));
			System.exit(1);
		}
		
		glValidateProgram(this.program);
		
		if (glGetProgrami(this.program, GL_VALIDATE_STATUS) != 1) {
			System.err.println(glGetProgramInfoLog(this.program));
			System.exit(1);
		}	
	}
	
	
	public void setUniform(String name, int value) {
		int location = glGetUniformLocation(this.program, name);
		
		if (location != -1) {
			glUniform1i(location, value);
		}
	}
	
	public void setUniform(String name, float value) {
		int location = glGetUniformLocation(this.program, name);
		
		if (location != -1) {
			glUniform1f(location, value);
		}
	}
	
	
	public void setUniform(String name, Matrix4f value) {
		int location = glGetUniformLocation(this.program, name);
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
		
		value.get(buffer);
		
		if (location != -1) {
			glUniformMatrix4fv(location, false, buffer);
		}
	}
	
	
	
	public void bind() {
		glUseProgram(this.program);
	}
	
	
	private String readFile(String filename) {
		StringBuilder string = new StringBuilder();
		
		BufferedReader br;
		
		try {
			br = new BufferedReader(new FileReader(new File("./assets/shaders/" + filename)));
			
			String line;
			
			while ((line = br.readLine()) != null) {
				string.append(line);
				string.append("\n");
			}
			
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return(string.toString());
	}
}
