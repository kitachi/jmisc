# Makefile - METS Java toolkit helper package Makefile
# Copyright (c) 2003-2004 by the President and Fellows of Harvard College

.SUFFIXES: .java .class .jar

BASE      = ../../../../../..
BIN       = ../bin
CLASSPATH = .:${BASE}:${BASE}/${BIN}/mets.jar

.java.class:
	javac -classpath ${CLASSPATH} $<

CLASSES = Any.class Base64.class Comment.class DateTime.class Enum.class \
	MdSec.class MetsElement.class MetsException.class MetsIDElement.class \
	MetsIdentifiable.class MetsReader.class MetsSerializable.class \
	MetsValidatable.class MetsValidator.class MetsVElement.class \
	MetsWriter.class MetsWriterState.class ObjectType.class PCData.class \
	PreformedXML.class ProcessingInstruction.class \
	Schema.class XmlStream.class

all: make_parser ${CLASSES}

make_parser:
	cd parser; make -k

Any.class: MetsElement.class
MetsElement.class: MetsException.class MetsSerializable.class
MetsIDElement.class: MetsElement.class MetsIdentifiable.class
MetsSerializable.class: MetsReader.class MetsWriter.class
MetsVElement.class: MetsElement.class MetsValidatable.class
MetsWriter.class: MetsWriterState.class
PCData.class: MetsSerializable.class
ProcessingInstruction.class: MetsSerializable.class
Comment.class: MetsSerializable.class

cleanclass:
	cd parser; make -k cleanclass
	rm -f *.class
