# BRJS Appcache Plugin

A plugin to enable [appcache](https://developer.mozilla.org/en/docs/HTML/Using_the_application_cache) support in BRJS applications.
The plugin automatically generates a manifest file listing the files to cache at a specific URL.

## Pre-requisites
- BladeRunnerJs 'installation'.
- Java Virtual Machine.

## Development

### Clone the repo and create your project
- Clone this repository.
- Configure the path to your BladeRunnerJS directory in `./gradle.properties` e.g.

    `brjsPath=C:/development/brjs`

### Using Eclipse or IntelliJ IDEA 
- If you are using either Eclipse or InteliJ IDEA for development set up your project files.
  - run `./gradlew eclipse` or `./gradle idea`.
  - For Eclipse the brjs-core source and JavaDocs will also be attached, for InteliJ this must be done manually. The src jar can be found in `<brjs-dir>/docs/src/`.
- Import the created project in to your IDE of choice.
  - This is generally done by selecting 'Import existing project'.
  - Once the project is imported the relevant dependencies should be added to the classpath for you.
 
### Build the plugin
- Run `./gradlew build` to build and test your plugin.
- Once the build has passed, your plugin is built and placed in the 'build/libs/' directory.
- You can run `./gradlew copyToBrjs` to automatically copy your jar to the conf/java directory to be picked up by BRJS.

## Usage

### Deployment
- Deploy to the apps/[application]/WEB-INF/lib folder for any apps you want to use the plugin.
> This is a requirement of BRJS v0.6, in the future the plugin deployment process may change.

### Enabling
-  To link it in to your application the plugin provides the `appcache.url` tag handler.
    - Your html tag should look something like `<html manifest="<@appcache.url @/>">`
- In dev, the manifest URL is blank by default so the appcache will not be used.
    - You can test the appcache in dev by manually specifying an appcache version in the config file. See the [Configuration](#configuration) section for details on how to do this.
- In prod, the manifest URL is always generated and points to a valid manifest.
    - The manifest will be given a new version every time you generate the prod files
    - The manifest can be given a specific version by specifying an appcache version in the config file. See the [Configuration](#configuration) section for details on how to do this.

### Configuration
- Appcache is configured at an aspect level, as different aspects will use different appcaches.
- A config file named `appcache.conf` is looked for in the aspect folder. It supports the following properties:
    - version: A specific version to use for the appcache manifest in dev or prod.