# Inventory Framework
[![](https://jitpack.io/v/Despical/InventoryFramework.svg)](https://jitpack.io/#Despical/InventoryFramework)
[![](https://img.shields.io/badge/JavaDocs-latest-lime.svg)](https://javadoc.io/doc/com.github.Despical/InventoryFramework)
[![](https://img.shields.io/badge/Wiki-click-lime.svg)](https://github.com/Despical/InventoryFramework/wiki)
[![Discord](https://img.shields.io/discord/719922452259668000.svg?color=lime&label=Discord)](https://discord.gg/Vhyy4HA)

An inventory framework for managing Minecraft GUIs.

This framework is a fork of [InventoryFramework](https://github.com/stefvanschie/IF) that has multiple version support and some addition features. Still based on a pane principle. This means that the GUI is divided into different types of panes which all behave differently. A GUI consists of multiple panes which can interact with each other.

Next to those panes, GUIs can also be created from XML files by simple loading them in. This allows for easy GUI creation with little code.

## Documentation
More information will be found on the [wiki page](https://github.com/Despical/InventoryFramework/wiki) soon. The [JavaDoc](https://javadoc.jitpack.io/com/github/Despical/InventoryFramework/latest/javadoc/index.html) can be browsed. Questions
related to the usage of Command Framework should be posted on my [Discord server](https://discord.com/invite/Vhyy4HA).

## Using Command Framework
The project isn't in the Central Repository yet, so specifying a repository is needed.<br>
To add this project as a dependency to your project, add the following to your pom.xml:

### Maven dependency
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.Despical</groupId>
    <artifactId>InventoryFramework</artifactId>
    <version>1.1.0</version>
</dependency>
```

Now in order to shade the project into your project, add the following to your pom.xml:
```XML
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.2</version>
    <configuration>
        <dependencyReducedPomLocation>${project.build.directory}/dependency-reduced-pom.xml</dependencyReducedPomLocation>
        <relocations>
            <relocation>
                <pattern>me.despical.inventoryframework</pattern>
                <shadedPattern>[YOUR PACKAGE].inventoryframework</shadedPattern>
            </relocation>
        </relocations>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
Replace [YOUR PACKAGE] with the top-level package of your project.

## Gradle dependency
To add this project as a dependency for your Gradle project, make sure your `dependencies` section of your build.gradle looks like the following:
```Groovy
dependencies {
    compile 'com.github.Despical:InventoryFramework:1.1.0'
    // ...
}
```
The project is in Maven Central, so ensure your `repositories` section resembles the following:
```Groovy
repositories {
    mavenCentral()
    // ...
}
```
In order to include the project in your own project, you will need to use the `shadowJar` plugin. If you don't have it already, add the following to the top of your file:
```Groovy
apply plugin: 'com.github.johnrengelman.shadow'
```
To relocate the project's classes to your own namespace, add the following, with [YOUR PACKAGE] being the top-level package of your project:
```Groovy
shadowJar {
    relocate 'me.despical', '[YOUR PACKAGE].inventoryframework'
}
```

## License
This code is under [Unlicense](https://unlicense.org/)

See the [LICENSE.txt](https://github.com/Despical/InventoryFramework/blob/main/LICENSE) file for required notices and attributions.

## Donations
You like this version of Inventory Framework? Then [donate](https://www.patreon.com/despical) back me to support the development.

## Contributing

I accept Pull Requests via GitHub. There are some guidelines which will make applying PRs easier for me:
+ No tabs! Please use spaces for indentation.
+ Respect the code style.
+ Create minimal diffs. If you feel the source code should be reformatted create a separate PR for this change.

You can learn more about contributing via GitHub in [contribution guidelines](CONTRIBUTING.md).

## Building from source
If you want to build this project from source, run the following from Git Bash:

    git clone https://github.com/Despical/InventoryFramework.git && cd InventoryFramework
    mvn clean package

The build can then be found in /InventoryFramework/target/<br>
And also don't forget to install Maven before building.
