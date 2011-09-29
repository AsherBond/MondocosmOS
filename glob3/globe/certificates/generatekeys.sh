#!/bin/bash

rm mykeystore

keytool \
    -genkeypair \
    -dname "cn=Diego Gomez Deck, ou=IGO Software S.L., o=Sun, c=US" \
    -alias codesigncert \
    -keypass igoadm \
    -keystore mykeystore \
    -storepass igoadm \
    -validity 10000
