#!/bin/bash

set -o nounset

CURRENT_VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }'`
TIMESTAMP=`date -u +%Y%m%d%H%M`
RELEASE_VERSION=${CURRENT_VERSION%%SNAPSHOT}$TIMESTAMP

echo "Switching to release branch: $CURRENT_VERSION -> $RELEASE_VERSION"
git checkout -b "release/$RELEASE_VERSION"

mvn versions:set "-DnewVersion=$RELEASE_VERSION" -DgenerateBackupPoms=false
git add pom.xml
git commit -m "Update version number for release"
mvn -DskipTests clean package install deploy
#gpg --delete-keys james.hu.ustc@hotmail.com
gpg --delete-secret-keys james.hu.ustc@hotmail.com

git tag -l "release/v$RELEASE_VERSION"
git push origin --tags

echo "Switching back to master branch"
git checkout master