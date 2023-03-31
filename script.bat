cd framework/src
javac -d ../bin utils/*.java
javac -d ../bin etu2075/annotation/*.java
javac -d ../bin etu2075/framework/*.java
javac -d ../bin etu2075/framework/servlet/*.java
cd ..
cd bin
jar -cvf ../../fw.jar *
copy D:\ITU_S4\framework\fw.jar D:\Tomcat\lib\fw.jar
copy D:\ITU_S4\framework\fw.jar D:\Tomcat\webapps\test-framework\WEB-INF\lib\fw.jar
cd ../../test-framework/src
javac -d ../WEB-INF/classes model/*.java
cd ..
jar -cvf D:/Tomcat/webapps/test-framework.war *