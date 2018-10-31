![figure](https://github.com/wangying8052/test/blob/master/11.png)

# How to use Decca
Decca can take a Maven based project (it should contain the complete Maven built project directory and file pom.xml) as input for analysis. The expected running environment is 64-bit Window operating system with JDK 1.7 or 1.8. **As Maven built projects need to download dependencies from Maven Central Repository, Decca cannot work offline.**

You can run Decca on our experimental subjects based on the following steps:

**Step 1**: Unzip the plugin-decca.zip to local directory. Recommended directory structure is:

>> D:\plugin-decca
     
>>     ├─decca-1.0.jar : 
     
>>     ├─decca-1.0.pom
   
>>     ├─soot-1.0.jar
    
>>     ├─apache-maven-3.2.5
   
>>     ├─script.jar

*Note: To facilitate testing, please keep the unzip directory to be consistent with the above example. It should be noted that the location of data (e.g, D:\plugin-decca) is not hardcoded, it can be replaced with user's actual unzip directory in the install commands.*

**Step 2**: Install Decca

(a) Execute the following Windows CMD and Ubuntu Terminator command to install soot:
>> Windows CMD command:

>> D:\plugin-decca\apache-maven-3.2.5\bin\mvn.bat install:install-file  -Dfile=D:\plugin-decca\soot-1.0.jar  -DgroupId=neu.lab  -DartifactId=soot -Dversion=1.0 -Dpackaging=jar

>> Ubuntu Terminator command：

>> sudo mvn install:install-file -Dfile=/plugin-decca/soot-1.0.jar -DgroupId=neu.lab -DartifactId=soot -Dversion=1.0 -Dpackaging=jar

(b) Execute the following Windows CMD and Ubuntu Terminator command to install Decca:
>> Windows CMD command:

>> D:\plugin-decca\apache-maven-3.2.5\bin\mvn.bat install:install-file  -Dfile=D:\plugin-decca\decca-1.0.jar  -DgroupId=neu.lab  -DartifactId=decca -Dversion=1.0 -Dpackaging=maven-plugin -DpomFile=D:\plugin-decca\decca-1.0.pom

>> Ubuntu Terminator command：

>> sudo mvn install:install-file -Dfile=/plugin-decca/decca-1.0.jar -DgroupId=neu.lab -DartifactId=decca -Dversion=1.0 -Dpackaging=maven-plugin -DpomFile=/plugin-decca/decca-1.0.pom

**Step 3**: Detect and assess the dependency conflict issues.

Execute the following Windows CMD and Ubuntu Terminator command to analyze the project:
>> Windows CMD command:

>> D:\plugin-decca\apache-maven-3.2.5\bin\mvn.bat -f=D:\RawData\Issue report dataset\Projects\hadoop-rel-release-3.0.0\hadoop-common-project\hadoop-minikdc\pom.xml -Dmaven.test.skip=true neu.lab:decca:1.0:detect -DresultPath=D:\Report\ -DdetectClass=true -e –Dappend=false –e

>> Ubuntu Terminator command：

>> sudo mvn -f=(example-path)/example-project/pom.xml -DresultPath=/resultPath/ -DsubdivisionLevel=false -Dmaven.test.skip=true neu.lab:decca:1.0:printRiskLevel –e

Then you can get the dependency issue report in your specified directory (e.g., **D:\Report\resultFile.xml**).

>>> **Command explanation:**
>>>>(1) -f=pom file : Specify the project under analysis;

>>>>(2) -DresultFilePath=output issue report directory : Output the issue report to the specified file;

>>>>(3) -DdetectClass=Boolean : Specify the tool whether reports the class level conflicts or not;

>>>>(4) -Dappend=Boolean : Specify the result output mode (whether in append mode or not). 


