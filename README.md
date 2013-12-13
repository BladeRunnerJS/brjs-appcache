# BRJS Plugin Bootstrap

A basic Gradle build file for bootstrapping BRJS Plugin development. Provides a task for initialising the src and resources directories along with a Gradle build file configured to produce Eclipse and Idea project files.

## Pre-requisites
- BladeRunnerJs 'installation'
- Java Virtual Machine

## Usage

### Clone the repo and create your project
- clone this repository and rename the directory to whatever name you wan't for your project
  - `git clone https://github.com/BladeRunnerJS/brjs-plugin-bootstrap.git MyCoolNewPlugin`
- configure the path to the BladeRunnerJS sdk directory
  - inside 'build.gradle' change the value of `ext.brjsSdkPath` so the path of your BladeRunnerJS sdk directory. 
- run `./gradlew init`, this will download the correct version of Gradle and create directories where you should place Java source code, resources and test code. It will also create a 'src/main/resources/META-INF/services' directory.
- Delete the 'init' task from 'build.gradle'. This step is optional, but you won't need the init task again now the directories have been created.
  - Delete everything from 'task init, ....' to the closing '}'.
- Import the created project in to your IDE of choice.
  - This is generally done by selecting 'Import existing project'.
  - Once the project is imported the relevant dependencies should be added to the classpath for you.
 
### Create your plugin
- Add your first class and implement the relevant BRJS plugin interface
  - This class should be created in 'src/main/java'.
  - Use the IDE to add any unimplemented methods for you.
- Inside src/main/resources/META-INF/services create a file with the same name as the full classpath of the BRJS plugin interface you are implementing.
  - For example: if you are implementing 'CommandPlugin', which is in the package 'org.bladerunnerjs.plugin', you would create the file 'src/main/resources/META-INF/services/org.bladerunnerjs.plugin.CommandPlugin'
- Inside that file add a line which is equal to the full classpath of your class
  - If your class is called 'MyCoolNewPlugin.java', in the 'com.cool.plugin' package, you would add the line 'com.cool.plugin.MyCoolNewPlugin'
- Get coding
  - Any source code should be in 'src/main/java', source resources in 'src/main/resources', test code in 'src/test/java' and test resources in 'src/test/java'.

### Build the plugin
- Run `./gradlew build` to build and test your plugin.
- Once the build has passed, your plugin is built and placed in the 'build/libs/' directory.

## More reading
- [BRJS getting started guide](http://bladerunnerjs.org/docs/use/getting_started/)
- Plugins (TODO)
- [Gradle](http://www.gradle.org/)
- [Gradle user guide and DSL reference](http://www.gradle.org/documentation)