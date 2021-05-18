# StackMob-5

StackMob redefined for 1.15 and 1.16. Uses NBT rather than databases/yaml files so performance will be greatly improved compared to previous versions of StackMob.

Spigot: https://www.spigotmc.org/resources/stackmob-enhance-your-servers-performance-without-the-sacrifice.29999/

StackMob is a plugin that reduces the strain of mobs on your server by 'stacking' them together, therefore you can have 1 entity instead of 30 entities. StackMob aims to try and not break vanilla mechanics, however this is not always pratical and/or possible.

It is recommended to use StackMob in conjunction with a farm limiter plugin for the best results.

### Jenkins: https://ci.codemc.io/job/Nathat23/job/StackMob-5/
# Building
In order to build StackMob, you will need to do the following.
- Clone the repository.
- Run 'mvn clean install' to compile.

# Contributing
Contributions are welcome. StackMob is licensed under the GPLv3.

# API
StackMob does have a few custom events that can be used. These are in the events subpackage.

Maven:
```xml
<repository>
    <id>CodeMC</id>     
    <url>https://repo.codemc.org/repository/maven-public/</url>
</repository>
```
```xml
<dependency>
    <groupId>uk.antiperson.stackmob</groupId>
    <artifactId>StackMob</artifactId>
    <version>5.5.3</version>
</dependency>
```