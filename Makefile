#This is the make file for JayCorp

compiler = /usr/bin/ecj -7 -target 1.7 -time -d build

build: bennyden

clean:
	rm -r build

buildFolder:
	mkdir build

build/JayCorp: buildFolder
	mkdir build/JayCorp

build/JayCorp/de: build/JayCorp
	mkdir build/JayCorp/de

build/JayCorp/de/bennyden: build/JayCorp/de
	mkdir build/JayCorp/de/bennyden

build/JayCorp/de/bennyden/coding: build/JayCorp/de/bennyden
	mkdir build/JayCorp/de/bennyden/coding
	
build/JayCorp/de/bennyden/coding/Base64Coding.class: build/JayCorp/de/bennyden/coding JayCorp/src/de/bennyden/coding/Base64Coding.java
	$(compiler) JayCorp/src/de/bennyden/coding/Base64Coding.java

build/JayCorp/de/bennyden/coding/package-info.class: build/JayCorp/de/bennyden/coding JayCorp/src/de/bennyden/coding/package-info.java
	$(compiler) JayCorp/src/de/bennyden/coding/package-info.java
	
bennyden: build/JayCorp/de/bennyden/coding/package-info.class build/JayCorp/de/bennyden/coding/Base64Coding.class

