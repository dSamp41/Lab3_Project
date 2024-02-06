./make.sh

cd build
jar cfe client.jar src.client.ClientMain src/client/*.class
jar cfe server.jar src.server.ServerMain src/server/*.class src/structures/*.class ../lib/gson-2.10.1.jar