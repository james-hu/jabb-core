#!/bin/sh
mvn install:install-file -Dfile=jta-1.0.1B.jar -DgroupId=javax.transaction -DartifactId=jta -Dversion=1.0.1B -Dpackaging=jar
mvn install:install-file -Dfile=ojdbc14.jar -DgroupId=com.oracle -DartifactId=ojdbc14 -Dversion=10.2.0.1.0 -Dpackaging=jar
mvn install:install-file -Dfile=mmseg4j-core-1.8.4-with-dic.jar -DgroupId=com.chenlb -DartifactId=mmseg4j-core -Dversion=1.8.4-with-dic -Dpackaging=jar

mvn install:install-file -Dfile=magnolia-core-5.2.jar -Dpackaging=jar -DgroupId=info.magnolia -DartifactId=magnolia-core -Dversion=5.2
mvn install:install-file -Dfile=magnolia-jaas-5.2.jar -Dpackaging=jar -DgroupId=info.magnolia -DartifactId=magnolia-jaas -Dversion=5.2
