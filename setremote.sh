#!/bin/bash

git remote -v

#prot=https
prot=git

git remote set-url origin $prot@github.com:/nhsdigitalmait/TKW-x.git

git remote -v
