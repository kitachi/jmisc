#!/bin/bash
rm ./src/main/java/sz/nextapp/web/OCRController.java
rm ./src/main/java/sz/nextapp/web/OCRController_Roo_Controller.aj

# TODO: copy all the customized szext files here
mvn clean package
mvn jetty:run
