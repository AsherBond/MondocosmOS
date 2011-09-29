#!/bin/bash


echo Java lines:
find . -iname "*.java" -exec cat {} \; | wc -l
echo


echo Python lines:
find . -iname "*.py" -exec cat {} \; | wc -l
echo


echo Beanshell lines:
find . -iname "*.bsh" -exec cat {} \; | wc -l
echo


echo GIT commits
git shortlog -sne
echo

if test -x /usr/bin/gitstats ; then
    echo Running gitstats...
    gitstats . temporary-data/gitstats
else
    echo === gitstats not installed: apt-get install gitstats ===
fi
