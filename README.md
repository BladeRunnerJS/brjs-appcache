# BRJS Appcache Plugin

A plugin to enable [appcache](https://developer.mozilla.org/en/docs/HTML/Using_the_application_cache) support in BRJS applications. The plugin automatically generates a manifest file listing the files to cache at a specific URL, so you don't need to manage it yourself.

## Quick start
- Download the [latest build of the plugin](lib/appcache-plugin.jar) (`lib/appcache-plugin.jar`).
- Copy the plugin JAR to the `apps/<your-app>/WEB-INF/lib` folder.
- Add the appcache plugin tag to your HTML element e.g. `<html manifest="<@appcache.url@/>">`
- `brjs war <your-app>`
- Deploy and see the appcache in action!

## Usage

### Installation
- Copy the plugin JAR to the `apps/<your-app>/WEB-INF/lib` folder for any apps you want to use the plugin.
> This is a requirement of BRJS v0.6, in the future the plugin deployment process may change.

### Enabling
- To link it in to your application the plugin provides the `appcache.url` tag handler. This tag will replaced with the URL to the manifest file, so you should set the `manifest` attribute on the `html` element to use the tag as its value. 
    - In other words your html element should look something like `<html manifest="<@appcache.url@/>">`
- In dev, the manifest URL is blank by default so the appcache will not be used.
    - You can test the appcache in dev by manually specifying an appcache version in the config file. See the [Configuration](#configuration) section for details on how to do this.
- In prod, the manifest URL is always generated and points to a valid manifest.
    - The manifest will be given a new version every time you generate the prod files.
    - The manifest can be given a specific version by specifying an appcache version in the config file. See the [Configuration](#configuration) section for details on how to do this.

> The  HTML `<base href=".." />` tag is incompatible with the appcache plugin. BRJS applications by default are created with the base tag in use, so *this will need to be removed before the appcache plugin will work*. This is OK - the use of the base tag was added by bladerunner to replicate some of the appcache functionality, but now we're using the real thing we don't need it any more!

<a name="configuration"></a>
### Configuration
- Appcache is configured at an aspect level, as different aspects will use different appcaches.
- A config file named `appcache.conf` is looked for in the aspect folder. It supports the following properties in YAML format:
    - `version` a specific version to use for the appcache manifest in dev or prod.
    - `languages` a comma separated list of valid [HTTP language tags](http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.10). If no languages are supplied, "en" files will be cached by default.

> The config file must either have a valid property name in it or be removed. Invalid property names or empty config files will produce errors in BRJS. The property *value* however can be blank.

### Issues
- BRJS supports loading of different files (e.g. CSS, JS, i18n) for different languages. The appcache plugin does support caching of these language files, but the list of languages you wish to cache must currently be supplied up front in the [configuration](#configuration). In a future BRJS release this will no longer be necessary and the list of languages will be generated for you.

## Development

### Clone the repo and create your project
- Clone this repository.
- Configure the path to your BladeRunnerJS directory in `./gradle.properties` e.g.

    `brjsPath=C:/development/brjs`

### Using Eclipse or IntelliJ IDEA 
If you are using either Eclipse or IntelliJ IDEA for development, follow the instructions below to set up your project files

- run `./gradlew eclipse` or `./gradlew idea`.
    - For Eclipse the brjs-core source and JavaDocs will also be attached, for IntelliJ this must be done manually. The src jar can be found in `<brjs-dir>/docs/src/`.
- Import the created project in to your IDE of choice.
    - This is generally done by selecting 'Import existing project'.
    - Once the project is imported the relevant dependencies should be added to the classpath for you.
 
### Build the plugin
- Run `./gradlew build` to build and test your plugin.
- Once the build has passed, your plugin is built and placed in the 'build/libs/' directory.
- You can run `./gradlew copyToBrjs` to automatically copy your jar to the conf/java directory to be picked up by BRJS.