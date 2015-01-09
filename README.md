# Burn HTTP!!!
This project help you to create huge amount `HTTP Request` to attack your target server.

# Usage
Compile this and take four parameters in your command line.
1. Url file path, the file contain attack url list.
2. Sync / Async mode. Sync request or async request.
3. Thread Count, the number of client you want emulate.
4. Number of Request Per Second, the amount of requests per second with each client you want to launch.

Each client will launch request from first url to last url in url list and loop until terminated.

# Command Line
java -jar BurnHTTP.jar <Url File Path> <sync/async> <Thread Count> <# of Request Per Second of Each Thread>

# Url File Format
each line contain a target url, ex:

----File Start------
http://google.com
https://google.com
http://yahoo.com
---------EOF--------