ifeq ($(OS),Windows_NT) 
    detected_OS := Windows
	remove = rmdir /Q /S
else
    detected_OS := $(shell sh -c 'uname 2>/dev/null || echo Unknown')
	remove = rm -r
endif


cleanCompileRun: clean compile run

compileRun: compile run

compile:
	javac -d "./bin/" -cp "./lib/*;./bin/*;./src/*" ./src/main/java/com/test/*.java

run:
	java -cp "./bin/;./lib/*" com.test.Main

clean:
	$(remove) "./bin"
	