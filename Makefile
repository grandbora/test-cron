clean:
	rm -rf cronparser project target

compile: clean
	scalac src/main/scala/matchers/*
	scalac src/main/scala/*.scala

run: compile
	scala cronparser.Main "$(CMD)"

run-without-compile:
	scala cronparser.Main "$(CMD)"

test:
	sbt test

sbt:
	sbt
