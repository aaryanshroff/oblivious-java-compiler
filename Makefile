# Variables
MAVEN_EXEC = mvn

.PHONY: all clean transform run test

all: compile transform run

compile:
	$(MAVEN_EXEC) clean compile

# Run the MemoryAccessTransformer to transform the bytecode
transform: compile
	$(MAVEN_EXEC) exec:java -Dexec.mainClass="com.example.MemoryAccessTransformer"

# Run the transformed class
run: transform
	java -cp target/classes com.example.App

# Run tests
test:
	$(MAVEN_EXEC) test

clean:
	$(MAVEN_EXEC) clean
	rm -rf target