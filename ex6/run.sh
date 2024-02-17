cd ./plugins-src/framework
javac *.java
jar cf ITFWords.jar ITFWords.class 
jar cf ITFCount.jar ITFCount.class 
# rm *.class
mv *.jar ./../../plugins/

cd ./../app/
javac -cp ".:./../../plugins/*" *.java
jar cf Words1.jar Words1.class
jar cf Words2.jar Words2.class
jar cf Count1.jar Count1.class
jar cf Count2.jar Count2.class
# rm *.class
mv *.jar ./../../plugins/

cd ./../../
javac -cp "./plugins/*" Twenty.java
java -cp ".:./plugins/*" Twenty ./../pride-and-prejudice.txt
