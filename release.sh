#!/bin/bash

set -o nounset

CURRENT_VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }'`
TIMESTAMP=`date -u +%Y%m%d%H%M`
RELEASE_VERSION=${CURRENT_VERSION%%SNAPSHOT}$TIMESTAMP

echo "Switching to release branch: $CURRENT_VERSION -> $RELEASE_VERSION"
git remote set-url origin git@github.com:james-hu/jabb-core.git
git checkout -b "release/$RELEASE_VERSION"

mvn versions:set "-DnewVersion=$RELEASE_VERSION" -DgenerateBackupPoms=false
git add pom.xml
git commit -m "Update version number for release"
git tag -l "release/v$RELEASE_VERSION"


mvn -DskipTests clean deploy

gpg --batch --delete-secret-keys `gpg --list-secret-keys --with-colons --fingerprint  james.hu.ustc@hotmail.com|sed -n 's/^fpr:::::::::\([[:alnum:]]\+\):/\1/p'`
cat ~/.m2/settings.xml | sed 's/<servers>.*<\/settings>/<\/settings>/g' > settings.xml
cp settings.xml ~/.m2/settings.xml
rm settings.xml

git push -u origin "release/$RELEASE_VERSION"
git push origin --tags
git status
