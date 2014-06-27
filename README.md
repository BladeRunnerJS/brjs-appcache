# BRJS Appcache Plugin

A plugin to enable [appcache](https://developer.mozilla.org/en/docs/HTML/Using_the_application_cache) support in [BRJS applications](http://bladerunnerjs.org/). The plugin automatically generates a manifest file listing the files to cache at a specific URL, so you don't need to manage it yourself.

## Quick start
- Download the [latest release of the plugin](https://github.com/caplin/brjs-appcache/releases/latest) (`appcache-plugin.jar`).
- Copy the plugin JAR to the BRJS `conf/java` folder.
- Copy the plugin JAR to the BRJS `apps/<your-app>/WEB-INF/lib` folder.
- Add the appcache plugin tag to your HTML element e.g. `<html manifest="<@appcache.url@/>">`

> Remember it's *disabled in dev by default*, so you'll either need to [enable it in dev](#enableInDev) or build a prod version of your app to see the appcache in action.

## Usage

### Installation
- Copy the plugin JAR to the BRJS `conf/java` folder.
- Copy the plugin JAR to the BRJS `apps/<your-app>/WEB-INF/lib` folder for any apps you want to use the plugin.

> This is a requirement of BRJS <= v0.9, in the future the plugin deployment process may change.

### Enabling
- To link it in to your application the plugin provides the `appcache.url` tag handler. This tag will replaced with the URL to the manifest file, so you should set the `manifest` attribute on the `html` element to use the tag as its value. 
    - In other words your html element should look something like `<html manifest="<@appcache.url@/>">`

> The HTML `<base href="..." />` tag is incompatible with the appcache plugin. BRJS <= v0.8 applications by default are created with the base tag in `index.html`, so *this will need to be removed before the appcache plugin will work*. This is OK - the use of the base tag was added by bladerunner to replicate some of the appcache functionality, but now you're using the real thing you don't need it any more!

<a name="enableInDev"></a>
#### Dev
In dev, the manifest URL is blank by default so the appcache will not be used.

- You can test the appcache in dev by manually specifying an appcache version in the config file. See the [Configuration](#configuration) section for details on how to do this.

#### Prod
In prod, the manifest URL is always generated and points to a valid manifest.
- The manifest will be given a new version every time you generate the prod files.
- The manifest can be given a specific version by specifying an appcache version in the config file. See the [Configuration](#configuration) section for details on how to do this.

<a name="configuration"></a>
### Configuration
- Appcache is configured at an aspect level, as different aspects will use different appcaches.
- A config file named `appcache.conf` is looked for in the `<aspect>/conf` folder. It supports the following properties in [YAML format](http://en.wikipedia.org/wiki/YAML#Examples) (i.e. `property: value`):
    - `version` a specific version to use for the appcache manifest in dev or prod.
- Only files for the listed locales are cached. The locales configuration is standard BRJS functionality, and can be found in the `<your-app>/app.conf` file.

## Development

### Clone the repo and configure
- Clone this repository.
- Create a `./gradle.properties` file in the plugin root directory with the path to your BRJS root directory:
    ```
    brjsPath=BRJS_DIRECTORY
    ```

    - Alternatively you can set the path by adding `-PbrjsPath=BRJS_DIRECTORY` to the command line arguments. This can be useful for things such as build scripts.

> If you don't have a local BRJS install you can prefix any of the gradle build commands with the `getBrjs` task. This will download the latest version of BRJS from the [BRJS GitHub](https://github.com/BladeRunnerJS/brjs) `master` branch and use that version when running all following commands. You can also supply the `brjsVersion` gradle property to use a version other than `master`. The value of the `brjsVersion` property is passed straight into `git checkout` so can be any valid git identifier (e.g. commit hashes, branch names, tag names).

> For example to test the plugin against BRJS tag `v0.9` you would run:

> `./gradlew getBrjs test -PbrjsVersion=v0.9`

> Note that as this tries to build BRJS from source, it's dependent on certain parts of the BRJS build and may not work with older versions of BRJS. You'll also need to ensure your PC meets the requirements for [building BRJS](https://github.com/BladeRunnerJS/brjs#brjs-core-toolkit-development).

### Using Eclipse or IntelliJ IDEA 
If you are using either Eclipse or IntelliJ IDEA for development, follow the instructions below to set up your project files

- run `./gradlew eclipse` or `./gradlew idea`.
    - For Eclipse the brjs-core source and JavaDocs will also be attached, for IntelliJ this must be done manually. The src jar can be found in `<brjs-dir>/docs/src/`.
- Import the created project in to your IDE of choice.
    - This is generally done by selecting 'Import existing project'.
    - Once the project is imported the relevant dependencies should be added to the classpath for you.
 
### Build the plugin
- Run `./gradlew build` to build and test your plugin.
- Once the build has passed, your generated plugin JAR is placed in the project `build/lib` directory.
- You can run `./gradlew copyToBrjs` to automatically build and copy your jar to the `apps/<your-app>/WEB-INF/lib` directories to be picked up by BRJS.

## BRJS Compatability
Ensure you use the correct version of the plugin for your BRJS version:

Plugin | BRJS
-------|-----
[2.0.0](https://github.com/caplin/brjs-appcache/releases/tag/2.0.0) | 0.10
Unsupported * | 0.9
[1.0.2](https://github.com/caplin/brjs-appcache/releases/tag/1.0.2) | 0.6-0.8
[1.0.1](https://github.com/caplin/brjs-appcache/releases/tag/1.0.1), [1.0.0](https://github.com/caplin/brjs-appcache/releases/tag/1.0.0) | 0.6-0.7
Untested | <= 0.5

> \* BRJS 0.9 made some changes that prevent appcache from working. These were [fixed in BRJS 0.10](https://github.com/BladeRunnerJS/brjs/issues/725).
