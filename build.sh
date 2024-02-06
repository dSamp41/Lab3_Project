./make.sh

cd build
jar cfe client.jar src.client.ClientMain src/client/*.class
#jar cfm server.jar serverManifest.txt src/server/*.class src/structures/*.class gson-2.10.1.jar
../buildServer.sh

mv client.jar ../artifacts
mv server.jar ../artifacts