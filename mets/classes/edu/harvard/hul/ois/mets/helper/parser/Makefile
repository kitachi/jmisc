# Makefile - METS Java toolkit helper package Makefile
# Copyright (c) 2003 by the President and Fellows of Harvard College

.SUFFIXES: .java .class .jar

BASE      = ../../../../../../..
BIN       = ../bin
CLASSPATH = .:${BASE}:${BASE}/${BIN}/mets.jar

.java.class:
	javac -classpath ${CLASSPATH} $<

CLASSES = Attribute.class Attributes.class Context.class Parser.class \
	ParserException.class State.class Type.class Token.class

all: ${CLASSES}

Attributes.class: Attribute.class
Context.class: State.class Type.class
Parser.class: Attributes.class Context.class Token.class
Token.class: Attributes.class Context.class

cleanclass:
	rm -f *.class
