Blake Parmeter coding challenge deployment instructions:

Environment: 
* Windows 10 64 bit

Prerequisites:
* Java JDK 1.8.121 or greater 
 -(http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
 - Make sure it's in your path environment variable and the JAVA_HOME environment variable is set
 
* Maven 3.2 or greater 
 -(https://maven.apache.org/download.cgi)
 -(https://maven.apache.org/guides/getting-started/windows-prerequisites.html)

1. Download the latest source code from the git repo below:
https://github.com/BlakeParmeter/SNDRCodeChallenge

2. If you downloaded the file as a zip, then extract the file

3. Open a powershell / cmd window in the directory with the pom.xml file. 
	cd -sndrCodeChallenge 

4. Run the command "mvn install" and wait for the program to finish.
	mvn install

5. navigate to the target directory 
	cd target

6. Run the newely compiled jar with dependencies and specify some arguments
	java -jar sndrCodeChallenge-1.0-jar-with-dependencies.jar https://raw.githubusercontent.com/dariusk/corpora/master/data/words/adjs.json https://raw.githubusercontent.com/dariusk/corpora/master/data/colors/crayola.json https://raw.githubusercontent.com/dariusk/corpora/master/data/humans/occupations.json
	
7. Stop the program using ctrl-c

8. Restart the program using step #6

9. Note: you can test the edge cases using to make the dictionary smaller:
	java -jar sndrCodeChallenge-1.0-jar-with-dependencies.jar https://raw.githubusercontent.com/dariusk/corpora/master/data/colors/crayola.json