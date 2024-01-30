rm build/src/client/*.class
rm build/src/server/*.class
rm build/src/structures/*.class

compilerOptions="javac -cp .:lib/gson-2.10.1.jar"
$compilerOptions -d ./build/client src/client/*.java
$compilerOptions -d ./build/server src/server/*.java
$compilerOptions -d ./build/structures src/structures/*.java