del SQLTool.jar
del *.class
javac *.java
jar cvfm SQLTool.jar manifest.mf *.java *.class
del *.class

