# Makefile - METS Java toolkit Makefile
# Copyright (c) 2003 by the President and Fellows of Harvard College

all: make_packages make_examples

make_packages:
	cd classes/edu/harvard/hul/ois/mets; make -k

make_examples:
	cd examples; make -k

cleanclass:
	cd classes/edu/harvard/hul/ois/mets; make -k cleanclass
	cd examples; make -k cleanclass
	rm -f bin/mets.jar

javadoc:
	javadoc -sourcepath classes -d doc \
		edu.harvard.hul.ois.mets \
		edu.harvard.hul.ois.mets.helper \
		edu.harvard.hul.ois.mets.helper.parser

########################################################################
# The following section is specific to Harvard's deployment process and
# should be ignored by other users.
########################################################################
PACKAGE   = mets
UTILS     = /src/ois-projects/utils
JAR       = bin/${PACKAGE}.jar

release: 
	# build the jar
	make cleanclass;
	make all;
	# make sure VERSION tag is valid
	${UTILS}/create_manifest.pl -v ${VERSION}
	# tag all the code in this package
	cvs tag ${VERSION} .	
	cvs update -r ${VERSION}
	# create the manifest and put it in the jar
	${UTILS}/create_manifest.pl -v ${VERSION} > manifest.template
	jar ufm ${JAR} manifest.template
	rm -fr manifest.template
	# refresh the local copy of code so no sticky tags/dates are set
	cvs update -A -P -d
	# copy the jar to its production location
	${UTILS}/move2prod_jar.pl ${JAR}
########################################################################
