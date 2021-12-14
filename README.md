# Secure Cloud Storage

## I. Setup Guide

### a. For Windows 

#### Install Java SDK 15:
  - Download the installer [here](https://www.oracle.com/java/technologies/javase-jdk15-downloads.html)
  - Run the installer
  - Control Panel -> System -> Advanced -> Environment Variables -> Add **C:\Program Files\Java\jdk-15\bin** to **PATH**

#### Install Maven:
  - Download the **Binary zip archive** [here](https://maven.apache.org/download.cgi)
  - Extract the archive to **C:\Program Files**
  - Control Panel -> System -> Advanced -> Environment Variables -> Add **C:\Program Files\apache-maven-3.8.1\bin** to **PATH**

#### Install Node.js
  - Download the Windows installer [here](https://nodejs.org/en/download/)
  - Run the installer

#### Install Git
  - Download and run the installer [here](https://git-scm.com/downloads)

#### Install IntelliJ IDEA
  - Download IntelliJ IDEA Community Edition [here](https://www.jetbrains.com/idea/download/#section=windows)


### b. For Linux (Ubuntu)

#### Install Java SDK 15:

```
  sudo apt-get install openjdk-15-jdk
  export JAVA_HOME=/usr/lib/jvm/openjdk-15-jdk
  export PATH=$PATH:$JAVA_HOME/bin
```

#### Install everything else:

```
  curl -sL https://deb.nodesource.com/setup_12.x | sudo -E bash -
  sudo apt-get install -y maven git nodejs
```

#### Install IntelliJ IDEA
  - Download IntelliJ IDEA Community Edition [here](https://www.jetbrains.com/idea/download/#section=linux)

### c. For macOS

#### Install Java SDK 15:
  - Download the macOS installer [here](https://www.oracle.com/java/technologies/javase-jdk15-downloads.html)
  - Run the installer

```
  export JAVA_HOME=/usr/lib/jvm/openjdk-15-jdk
  export PATH=$PATH:$JAVA_HOME/bin
```

#### Install Homebrew:

```
  /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

#### Install Maven and Node.js:

```
  brew install node maven
```

#### Install IntelliJ IDEA
  - Download IntelliJ IDEA Community Edition [here](https://www.jetbrains.com/idea/download/#section=mac)


## II. Download AWS Java SDK

- You can download the SDK [here](https://sdk-for-java.amazonwebservices.com/latest/aws-java-sdk.zip). Unzip it into a local directory of your choice (e.g. C:\Users\your_name\Desktop\AWS_lib).

- Go into the **third-party/lib** folder in the extracted folder (e.g. C:\Users\your_name\Desktop\AWS_lib\aws-java-sdk-1.11.974\third-party\lib). Delete the **freemarker-2.3.9.jar** file cause it is not compatible with Vaadin.

## III. Clone the project to a local folder

```
  git clone https://github.gatech.edu/cdong49/SecureCloudStorage.git
```

## IV. IntelliJ setup

- First, open the folder as a project in IntelliJ. 
- Wait until IntelliJ finish importing all dependencies (this might take a bit)

### Project Structure

- File -> Project Structure -> Project. Check that the **Project SDK** is Java SDK 15. If it is not, select Java SDK 15 by **Edit** and browse to the Java SDK path
- File -> Project Structure -> Libraries.
  - Click the **+** icon and choose **Java**. Select the **lib** path from the SDK folder you unzipped earlier (e.g. C:\Users\your_name\Desktop\AWS_lib\aws-java-sdk-1.11.974\lib).
  - Click the **+** icon and choose **Java**. Select the **lib** path from the third party folder in the SDK folder you unzipped earlier (e.g. C:\Users\your_name\Desktop\AWS_lib\aws-java-sdk-1.11.974\third-party\lib).
  - Click Apply
- File -> Project Structure -> Modules. Select **lib** and **lib1** that we just added. Click Apply.

![alt text](/readme_resources/AWS_lib.PNG)


### Build Project

- Build the project by clicking on the green hammer button or go to Build -> Build Project
- NOTE: At this point, you might see a loading bar at the bottom right screen, which says something like "indexing lib". This means IntelliJ is indexing the AWS SDK lib folders to import all necessary dependencies. Please wait until this is finished and the loading bar goes away before proceeding. This might take around 2 minutes.

### Maven install

- Click on the **Maven** tab on the upper right corner.
- Select SecureCloudStorage -> Lifecycle -> install
- This step is buggy, so it might giving you an error saying **com.amazonaws.auth not found**. If it does, just run install again.


### Run the Project

- Click on the src/main/java/com/example/application/Application.java
- Click on the green arrow on the main function to run it.

![alt text](/readme_resources/run_main.png)

- NOTE: It will take a while for Vaadin to compile and run since AWS libraries are really really heavy (sad react). Please be patient! Should take around 1 to 1.5 minutes

- Once it is done, goto **localhost:8080** on a browser of your choosing to access the app.


### Code base

#### Front end views
- Front end views of our web service are supported by [Vaadin](https://vaadin.com/), which is a platform to build UI in Java.
- Each views is stored in a package in this folder **src\main\java\com\example\application\views**

#### Backend user storage
- Because Vaadin is compatible with [Spring Boot](https://spring.io/projects/spring-boot), we integrate our application to use Spring Boot for our local database.
- Entities such as User are stored in this folder **src\main\java\com\example\application\data\entity**
- Services such as Database and Authentication Service is stored in this folder **src\main\java\com\example\application\data\service**

#### AWS Controller
- For our cloud storage, we decide to go with [AWS S3](https://aws.amazon.com/s3/). Thanks to the AWS team releasing a Java SDK for us to interact with our S3 server, we are able to develop our project completely in Java. 
- The AWS controller is stored in **src\main\java\com\example\application\data\AWS**

#### Cryptography
- The project cryptography (RSA and ChaCha20) is stored in **src\main\java\com\example\application\data\crypto**
- The chunking encryption scheme to encrypt really large files in a short period of time is taken from [my earlier work](https://github.com/cdong1012/MemeCryptor) when I developed a ransomware.

