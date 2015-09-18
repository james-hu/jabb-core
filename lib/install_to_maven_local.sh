#!/bin/sh
mvn -q install:install-file -Dfile=ojdbc14.jar -DgroupId=com.oracle -DartifactId=ojdbc14 -Dversion=10.2.0.1.0 -Dpackaging=jar

mvn -q install:install-file -Dfile=magnolia-core-5.2.jar -Dpackaging=jar -DgroupId=info.magnolia -DartifactId=magnolia-core -Dversion=5.2
mvn -q install:install-file -Dfile=magnolia-jaas-5.2.jar -Dpackaging=jar -DgroupId=info.magnolia -DartifactId=magnolia-jaas -Dversion=5.2

mvn -q install:install-file -Dfile=myschedule-web-3.2.1.1.0-classes.jar -Dclassifier=classes -Dpackaging=jar -DgroupId=myschedule -DartifactId=myschedule-web -Dversion=3.2.1.1.0
mvn -q install:install-file -Dfile=myschedule-web-config-3.2.1.1.0.jar -Dpackaging=jar -DgroupId=myschedule -DartifactId=myschedule-web-config -Dversion=3.2.1.1.0
mvn -q install:install-file -Dfile=myschedule-quartz-extra-3.2.1.1.0.jar -Dpackaging=jar -DgroupId=myschedule -DartifactId=myschedule-quartz-extra -Dversion=3.2.1.1.0
