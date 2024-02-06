GSON_EXTRACT_FOLDER="example"

mkdir -p $GSON_EXTRACT_FOLDER
cp gson-2.10.1.jar $GSON_EXTRACT_FOLDER

cd $GSON_EXTRACT_FOLDER
jar -xf gson-2.10.1.jar
rm -r META-INF gson-2.10.1.jar

cd ..   #move back to build
jar cfm server.jar serverManifest.txt src/server/*.class src/structures/*.class -C $GSON_EXTRACT_FOLDER .
rm -r $GSON_EXTRACT_FOLDER
