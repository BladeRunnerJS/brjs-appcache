# BRJS Appcache Plugin

A plugin to enable [appcache](https://developer.mozilla.org/en/docs/HTML/Using_the_application_cache) support in [BRJS applications](http://bladerunnerjs.org/). The plugin automatically generates a manifest file listing the files to cache at a specific URL, so you don't need to manage it yourself.

## Quick start
- Download the [latest release of the plugin](https://github.com/caplin/brjs-appcache/releases/latest) (`appcache-plugin.jar`).
- Copy the plugin JAR to the `apps/<your-app>/WEB-INF/lib` folder.
- Add the appcache plugin tag to your HTML element e.g. `<html manifest="<@appcache.url@/>">`
- That's it, your app is now appcache ready!

> Remember it's *disabled in dev by default*, so you'll either need to [enable it in dev](#enableInDev) or build a prod version of your app to see the appcache in action.

## Usage

### Installation
- Copy the plugin JAR to the `apps/<your-app>/WEB-INF/lib` folder for any apps you want to use the plugin.

> This is a requirement of BRJS <= v0.9, in the future the plugin deployment process may change.

### Enabling
- To link it in to your application the plugin provides the `appcache.url` tag handler. This tag will replaced with the URL to the manifest file, so you should set the `manifest` attribute on the `html` element to use the tag as its value. 
    - In other words your html element should look something like `<html manifest="<@appcache.url@/>">`
- <a name="enableInDev"></a>In dev, the manifest URL is blank by default so the appcache will not be used.
    - You can test the appcache in dev by manually specifying an appcache version in the config file. See the [Configuration](#configuration) section for details on how to do this.
- In prod, the manifest URL is always generated and points to a valid manifest.
    - The manifest will be given a new version every time you generate the prod files.
    - The manifest can be given a specific version by specifying an appcache version in the config file. See the [Configuration](#configuration) section for details on how to do this.

> The HTML `<base href="..." />` tag is incompatible with the appcache plugin. BRJS <= v0.8 applications by default are created with the base tag in `index.html`, so *this will need to be removed before the appcache plugin will work*. This is OK - the use of the base tag was added by bladerunner to replicate some of the appcache functionality, but now you're using the real thing you don't need it any more!

<a name="configuration"></a>
### Configuration
- Appcache is configured at an aspect level, as different aspects will use different appcaches.
- A config file named `appcache.conf` is looked for in the `<aspect>/conf` folder. It supports the following properties in [YAML format](http://en.wikipedia.org/wiki/YAML#Examples) (i.e. `property: value`):
    - `version` a specific version to use for the appcache manifest in dev or prod.
- Only files for the listed locales are cached. The locales configuration is standard BRJS functionality, and can be found in the `<your-app>/app.conf` file.

## Development

### Clone the repo and configure
- Clone this repository.
- Configure the path to your BladeRunnerJS directory in `./gradle.properties` e.g.
```
brjsPath=C:/development/brjs
```

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
- You can run `./gradlew copyToBrjs` to automatically copy your jar to the `apps/<your-app>/WEB-INF/lib` directories to be picked up by BRJS.
