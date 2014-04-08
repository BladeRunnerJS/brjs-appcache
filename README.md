# BRJS Appcache Plugin

A plugin to enable [appcache](https://developer.mozilla.org/en/docs/HTML/Using_the_application_cache) support in BRJS applications.

## Pre-requisites
- BladeRunnerJs 'installation'
- Java Virtual Machine

## Development

### Clone the repo and create your project
- Clone this repository
- Configure the path to your BladeRunnerJS directory in `./gradle.properties` e.g.

    `brjsPath=C:/development/brjs`

### Using Eclipse or IntelliJ IDEA 
- If you are using either Eclipse or InteliJ IDEA for development set up your project files
  - run `./gradlew eclipse` or `./gradle idea`
  - For Eclipse the brjs-core source and JavaDocs will also be attached, for InteliJ this must be done manually. The src jar can be found in `<brjs-dir>/docs/src/`.
- Import the created project in to your IDE of choice.
  - This is generally done by selecting 'Import existing project'.
  - Once the project is imported the relevant dependencies should be added to the classpath for you.
 
### Build the plugin
- Run `./gradlew build` to build and test your plugin.
- Once the build has passed, your plugin is built and placed in the 'build/libs/' directory.
- You can run `./gradlew copyToBrjs` to automatically copy your jar to the conf/java directory to be picked up by BRJS.