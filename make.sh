rm -f build/src/client/*.class
rm -f build/src/server/*.class
rm -f build/src/structures/*.class

compilerOptions="javac -cp .:lib/gson-2.10.1.jar"
$compilerOptions -d build/ src/client/*.java
$compilerOptions -d build/ src/server/*.java
$compilerOptions -d build/ src/structures/*.java