rm src/client/*.class
rm src/server/*.class
rm src/structures/*.class

compilerOptions="javac -cp .:lib/gson-2.10.1.jar"
$compilerOptions src/client/*.java
$compilerOptions src/server/*.java
$compilerOptions src/structures/*.java