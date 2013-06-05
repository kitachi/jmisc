# Makefile - METS Java toolkit examples Makefile.
# Copyright (c) 2003-2005 by the President and Fellows of Harvard College


.SUFFIXES: .java .class

BIN       = ../bin
CLASSPATH = .:${BIN}/mets.jar

.java.class:
	javac -classpath ${CLASSPATH} $<

CLASSES = Copy.class Validate.class Write.class

all: ${CLASSES}

cleanclass:
	rm -f *.class
