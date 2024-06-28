# Variables
MAVEN_EXEC = mvn
JAR_NAME = oblivious-java-compiler-1.0-SNAPSHOT.jar

.PHONY: all clean compile build-cli transform run test

all: compile build-cli

compile:
	$(MAVEN_EXEC) clean compile

# Build the CLI JAR
build-cli: compile
	$(MAVEN_EXEC) package
	@if [ ! -f target/$(JAR_NAME) ]; then \
		echo "Error: JAR file not found in target directory"; \
		exit 1; \
	fi

# Run the MemoryAccessTransformer CLI
transform: build-cli
	@echo "Usage: make transform CLASS_NAME=com.example.YourClass CLASS_PATH=path/to/your/class/file.class"
	@if [ -z "$(CLASS_NAME)" ] || [ -z "$(CLASS_PATH)" ]; then \
		echo "Error: CLASS_NAME and CLASS_PATH must be provided"; \
		exit 1; \
	fi
	java -jar target/$(JAR_NAME) $(CLASS_NAME) $(CLASS_PATH)

# Run the transformed class
run: transform
	java -cp target/classes $(CLASS_NAME)

# Run tests
test:
	$(MAVEN_EXEC) test

clean:
	$(MAVEN_EXEC) clean
	rm -rf target