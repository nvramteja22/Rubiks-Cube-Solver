#!/bin/bash
echo "Compiling Java project..."
mkdir -p bin
javac -cp "lib/*" -d bin src/main/java/com/[your-username]/rubikcube/*.java
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
fi