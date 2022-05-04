ifeq ($(OS),Windows_NT) 
    detected_OS := Windows
	remove = rmdir /Q /S
	compileClasspath = "./lib/*;./bin/*;./src/*"
	executeClasspath = "./bin/;./lib/*"
else
    detected_OS := $(shell sh -c 'uname 2>/dev/null || echo Unknown')
	remove = sudo rm -f -r
	compileClasspath = "./lib/*:./bin/*:./src/*"
	executeClasspath = "./bin/:./lib/*"
endif


cleanCompileRun: clean compile run

compileRun: compile run

compile:
	javac -d "./bin/" -cp $(compileClasspath) ./src/main/java/com/test/*.java

run:
	java -cp $(executeClasspath) com.test.Main

clean:
	$(remove) "./bin"
	
