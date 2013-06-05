# Makefile - METS Java toolkit Makefile
# Copyright (c) 2003-2006 by the President and Fellows of Harvard College

.SUFFIXES: .java .class .jar

BASE      = ../../../../..
BIN       = ../bin
JAR       = ${BIN}/mets.jar
METS      = ${BASE}/${JAR}
CLASSPATH = .:${BASE}:${METS}

.java.class:
	javac -classpath ${CLASSPATH} $<

CLASSES = Actuate.class AmdSec.class Agent.class AltRecordID.class Area.class \
	Behavior.class BehaviorSec.class Betype.class BinData.class \
	Checksumtype.class \
	Div.class DigiprovMD.class DmdSec.class \
	Exttype.class \
	FContent.class FLocat.class File.class FileGrp.class FileSec.class \
	Fptr.class \
	InterfaceDef.class \
	Loctype.class \
	MdRef.class Mdtype.class MdWrap.class Mechanism.class \
	Mets.class MetsHdr.class Mptr.class \
	Name.class Note.class \
	Par.class \
	RightsMD.class Role.class \
	Seq.class Shape.class Show.class SmLink.class SourceMD.class \
	Stream.class StructLink.class StructMap.class \
	TechMD.class TransformFile.class Transformtype.class Type.class \
	XmlData.class

all: make_helper ${METS}

make_helper:
	cd helper; make -k

${METS}: make_helper ${CLASSES}
	cd ${BASE}; jar cf ${JAR} \
		edu/harvard/hul/ois/mets/*.class \
		edu/harvard/hul/ois/mets/helper/*.class \
		edu/harvard/hul/ois/mets/helper/parser/*.class

cleanclass:
	rm -f ${JAR}
	cd helper; make -k cleanclass
	rm -f *.class
