# Makefile for Maven Java Project

# Variables
MAVEN_EXEC = mvn
JAVA_MAIN_CLASS = com.example.TransformerTest

.PHONY: all clean compile run

all: compile run

# Maven targets
compile:
	$(MAVEN_EXEC) compile

run: compile
	$(MAVEN_EXEC) exec:java -Dexec.mainClass=$(JAVA_MAIN_CLASS)

clean:
	$(MAVEN_EXEC) clean
	rm -f target/classes/com/example/YourClassToTransform.class
	rm -f target/classes/com/example/PathORAM.class
	rm -f target/classes/com/example/MemoryAccessTransformer.class
	rm -f target/classes/com/example/TransformerTest.class