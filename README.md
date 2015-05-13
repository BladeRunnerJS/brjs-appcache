# BRJS Appcache Plugin

A plugin to enable [appcache](https://developer.mozilla.org/en/docs/HTML/Using_the_application_cache) support in [BRJS applications](http://bladerunnerjs.org/).
The plugin automatically generates a manifest file listing the files to cache at a specific URL, so you don't need to manage it yourself.

## Quick start
- Download the latest `appcache-plugin.jar` at https://github.com/BladeRunnerJS/brjs-appcache/releases
- Copy the plugin JAR to the BRJS `conf/java` folder.
- Add the appcache plugin tag to your HTML element e.g. `<html manifest="<@appcache.url@/>">`

## Usage

### Installation
- Copy the plugin JAR to the BRJS `conf/java` folder.

<a name="enabling"></a>
### Enabling
- The plugin provides the `appcache.url` tag handler. This tag will replaced with the URL to the manifest file, so you should set the `manifest` attribute on the `html` element to use the tag as its value.
    - To do this change the `html` element to `<html manifest="<@appcache.url@/>">`

<a name="configuration"></a>
### Configuration
- By default thje appcache is disabled in 'dev' and a URL that returns a 404 response will be used to disable the cache
- The appcache version is configured via the `BRJS` version property.
- To enable the appcache in 'dev' the version should be changed to a numbered version. To do this run the `serve` command 
with the 'version' argument, for example `brjs serve -v 1.2.3`. A timestamp will be appended to the 
 version to ensure versions are unique. 
  - The timestamp is calculated when the serve is started and will not
 change on subsequent reloads. This is to ensure the version remains static in the manifest as browser's
 will throw an exception is the manifest file changes while assets are being downloaded. To change the version
 the server will need to be restarted for a new timestamp to be generated.
- When building an app for production the version can be supplied via the 'version' argument, for example `brjs build-app foo -v 1.2.3`. 
 As with the serve command a timestamp is also appended to this version. 

## Development

### Clone the repo and configure
- Clone this repository.
- Create a `./gradle.properties` file in the plugin root directory with the path to your BRJS root directory:
    ```
    brjsPath=BRJS_DIRECTORY
    ```

- Alternatively you can set the path by adding `-PbrjsPath=BRJS_DIRECTORY` to the command line arguments. This can be useful for things such as build scripts.

### Using Eclipse or IntelliJ IDEA 
If you are using either Eclipse or IntelliJ IDEA for development, follow the instructions below to set up your project files

- run `./gradlew eclipse` or `./gradlew idea`.
- Import the created project in to your IDE of choice.
    - This is generally done by selecting 'Import existing project'.
    - Once the project is imported the relevant dependencies should be added to the classpath for you.
 
### Build the plugin
- Run `./gradlew build` to build and test your plugin.
- Once the build has passed, your generated plugin JAR is placed in the project `build/lib` directory.
- You can run `./gradlew copyToBrjs` to automatically build and copy your jar to the `conf/java` directory inside of BRJS.

### Releasing
The release process is very similar to the BRJS project.
- Create a new annotated tag. It must be annotated as `git describe` which is used for version calulcation will only match annotated tags.
- Run `gradle clean build` to generate the artifact which will be placed in `build/libs`
- Push the tag using `git push <remote> <tagname`.
- Manually create the release and upload the artifact via GitHub.

## BRJS Compatability
Ensure you use the correct version of the plugin for your BRJS version:

Plugin | BRJS
-------|-----
[3.x](https://github.com/BladeRunnerJS/brjs-appcache/releases/tag/latest) | 1.0+
[2.x](https://github.com/BladeRunnerJS/brjs-appcache/releases/tag/2.1.0) | 0.10+
Unsupported * | 0.9
[1.0.2](https://github.com/caplin/brjs-appcache/releases/tag/1.0.2) | 0.6-0.8
[1.0.1](https://github.com/caplin/brjs-appcache/releases/tag/1.0.1), [1.0.0](https://github.com/caplin/brjs-appcache/releases/tag/1.0.0) | 0.6-0.7
Untested | <= 0.5

> \* BRJS 0.9 made some changes that prevent appcache from working. These were [fixed in BRJS 0.10](https://github.com/BladeRunnerJS/brjs/issues/725).
