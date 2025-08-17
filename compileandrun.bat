@echo off
echo Compiling Java project...
if not exist bin mkdir bin
javac -cp "lib/*" -d bin src/com/ramteja/rubik/*.java
if %ERRORLEVEL% == 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
)
echo Running Rubik Cube Solver...
java -cp "bin;lib/*" com.ramteja.rubik.RubikCubeGUI