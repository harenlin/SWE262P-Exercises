## HOW TO RUN
You can follow the command:
### Part1. Create the interfaces
```shell
cd ./plugins-src/framework
javac *.java
jar cf ITFWords.jar ITFWords.class 
jar cf ITFCount.jar ITFCount.class 
rm *.class
mv *.jar ./../../plugins/
```

### Part2. Implement the plugins
```
cd ./../app/
javac -cp ".:./../../plugins/*" *.java
jar cf Words1.jar Words1.class
jar cf Words2.jar Words2.class
jar cf Count1.jar Count1.class
jar cf Count2.jar Count2.class
rm *.class
mv *.jar ./../../plugins/
```

### Part3. Run the application
Before running commands, please modify the ```config.properties``` file to generate the corresponding results.
```
cd ./../../
javac -cp ".:./plugins/*" Twenty.java
java -cp ".:./plugins/*" Twenty ./../pride-and-prejudice.txt
```

### P.S. 
The above commands has been wrapped into the shell script ```run.sh```, you can simply modify the ```config.properties``` file, and run ```bash run.sh```.

