package project.rendering;

import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

// class to create Shader objects. they handle the OpenGL implementation of shaders, which are small programs that run on GPU.
// usually used to define how vertices should be rendered to screen
public class Shader implements IShader {
	// id of the shader in OpenGL
	private int program;
	// id of the vertex shader in OpenGL (vertex shaders are the programs that handle the vertices to be rendered)
	private int vertID;
	// id of the fragment shader in OpenGL (fragment shaders are the programs that handle the fragments (similar to pixels) to be rendered)
	private int fragID;
	
	// Constructor
	public Shader(String filename) {
		// create a new OpenGL program
		this.program = glCreateProgram();
		// create a new OpenGL vertex shader
		this.vertID = glCreateShader(GL_VERTEX_SHADER);
		// link the source location of the vertex shader to OpenGL
		glShaderSource(this.vertID, readFile(filename + ".vert"));
		// compile the vertex shader
		glCompileShader(this.vertID);
		// check for errors during the compilation of the shader
		if (glGetShaderi(this.vertID, GL_COMPILE_STATUS) != 1) {
			System.err.println(glGetShaderInfoLog(this.vertID));
			System.exit(1);
		}
		// create a new OpenGL fragment shader
		this.fragID = glCreateShader(GL_FRAGMENT_SHADER);
		// link the source location of the fragment shader to OpenGL
		glShaderSource(this.fragID, readFile(filename + ".frag"));
		// compile the fragment shader
		glCompileShader(this.fragID);
		// check for errors during the compilation of the shader
		if (glGetShaderi(this.fragID, GL_COMPILE_STATUS) != 1) {
			System.err.println(glGetShaderInfoLog(this.fragID));
			System.exit(1);
		}
		// link the vertex and fragment shaders to the current program
		glAttachShader(this.program, this.vertID);
		glAttachShader(this.program, this.fragID);
		// bind the location for attributes to the shaders (in our case, slot 0 is for vertices, slot 1 is for textures)
		glBindAttribLocation(this.program, 0, "vertices");
		glBindAttribLocation(this.program, 1, "textures");
		// link the program to be the current program running
		glLinkProgram(this.program);
		// check for linking errors in the program
		if (glGetProgrami(this.program, GL_LINK_STATUS) != 1) {
			System.err.println(glGetProgramInfoLog(this.program));
			System.exit(1);
		}
		// validate the program
		glValidateProgram(this.program);
		// check for validation errors in the program
		if (glGetProgrami(this.program, GL_VALIDATE_STATUS) != 1) {
			System.err.println(glGetProgramInfoLog(this.program));
			System.exit(1);
		}	
	}
	
	// method for setting a uniform (uniforms are variables that can be passed to a shader from the calling program)
	public void setUniform(String name, int value) {
		// get the location of the uniform by its name
		int location = glGetUniformLocation(this.program, name);
		// in case there aren't errors
		if (location != -1) {
			// pass the value to the uniform
			glUniform1i(location, value);
		}
	}
	// same for float
	public void setUniform(String name, float value) {
		int location = glGetUniformLocation(this.program, name);
		
		if (location != -1) {
			glUniform1f(location, value);
		}
	}
	// same for Matrix4f
	public void setUniform(String name, Matrix4f value) {
		int location = glGetUniformLocation(this.program, name);
		// format the matrix into a 16 size buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
		value.get(buffer);
		
		if (location != -1) {
			glUniformMatrix4fv(location, false, buffer);
		}
	}
	
	// method for binding the shader to be the one to be used for rendering 
	public void bind() {
		glUseProgram(this.program);
	}
	
	// method for reading a file and convert it into a string
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
