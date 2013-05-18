#!/usr/bin/env bash

# Script to automate tests of the Merkle Hellman crypto system on various file types

# Move executable from dist directory
cp -f ../dist/Merkle-Hellman.jar .

echo "Testing default and help..."
echo "================================================================================"
java -jar Merkle-Hellman.jar
java -jar Merkle-Hellman.jar help

echo "Testing keygen..."
echo "================================================================================"
java -jar Merkle-Hellman.jar keygen
java -jar Merkle-Hellman.jar --keygen
java -jar Merkle-Hellman.jar -k

echo "Testing plain text..."
echo "================================================================================"
java -jar Merkle-Hellman.jar -k
cat plain.txt | java -jar Merkle-Hellman.jar -e public.key > plain.encrypted
cat plain.encrypted | java -jar Merkle-Hellman.jar -d private.key > plain.decrypted

echo "Testing image..."
echo "================================================================================"
java -jar Merkle-Hellman.jar -k
cat image.jpg | java -jar Merkle-Hellman.jar -e public.key > image.encrypted
cat image.encrypted | java -jar Merkle-Hellman.jar -d private.key > image.decrypted

echo "Testing song..."
echo "================================================================================"
java -jar Merkle-Hellman.jar -k
cat song.mp3 | java -jar Merkle-Hellman.jar -e public.key > song.encrypted
cat song.encrypted | java -jar Merkle-Hellman.jar -d private.key > song.decrypted

echo "Opening image and playing song"
echo "================================================================================"
vlc song.decrypted &
open image.decrypted &

