#!/bin/bash

set -o nounset

gpg --batch --delete-secret-keys `gpg --list-secret-keys --with-colons --fingerprint  james.hu.ustc@hotmail.com|sed -n 's/^fpr:::::::::\([[:alnum:]]\+\):/\1/p'`
cat ~/.m2/settings.xml | sed 's/<servers>.*<\/settings>/<\/settings>/g' > settings.xml
cp settings.xml ~/.m2/settings.xml
rm settings.xml

