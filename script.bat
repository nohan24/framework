cd framework/src
javac -d  ../bin utils/*.java
javac -d  ../bin etu2075/annotation/*.java
javac -d  ../bin etu2075/framework/*.java
javac -d  ../bin etu2075/*.java
javac -d  ../bin etu2075/framework/servlet/*.java
cd ..
cd bin
jar -cvf D:\ITU_S4\framework\fw.jar *
copy D:\ITU_S4\framework\fw.jar D:\Tomcat\lib\fw.jar
cd ../../test-framework
mkdir test-fw
cd test-fw
mkdir WEB-INF
cd WEB-INF
mkdir classes
mkdir lib
cd ../..
copy D:\ITU_S4\framework\fw.jar D:\ITU_S4\framework\test-framework\test-fw\WEB-INF\lib\fw.jar
copy D:\ITU_S4\framework\test-framework\web.xml D:\ITU_S4\framework\test-framework\test-fw\WEB-INF\web.xml
cd src
javac -d ../test-fw/WEB-INF/classes model/*.java
cd ../test-fw
copy D:\ITU_S4\framework\test-framework\*.jsp D:\ITU_S4\framework\test-framework\test-fw\*.jsp
jar -cvf D:/Tomcat/webapps/test-fw.war *
cd D:\ITU_S4\framework\test-framework
rmdir /s /q test-fw
    

