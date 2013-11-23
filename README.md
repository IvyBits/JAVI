JAVI - Java Audio/Video Interface
=================================

Media integration for Java.

Website: [http://dev.ivybits.tk/projects/javi](http://dev.ivybits.tk/projects/javi)
<br>
Issues: [http://dev.ivybits.tk/projects/javi/issues](http://dev.ivybits.tk/projects/javi/issues)

As a Maven Dependency
---------------------

The easiest way to get started with JAVI is with it as a dependency in your [Maven](http://maven.apache.org/download.html) project.
You must first add our Maven repository, then add JAVI as a dependency.

```xml
<dependencies>
    <dependency>
        <groupId>tk.ivybits.javi</groupId>
        <artifactId>javi</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>IvyBits</id>
        <url>http://maven.ivybits.tk</url>
    </repository>
</repositories>
```

Coding and Pull Requests Guidelines
-----------------------------------

* We follow the [Oracle coding conventions](http://www.oracle.com/technetwork/java/codeconv-138413.html).
  * 4 spaces; not tabs.
  * No trailing whitespaces.
  * No 80 column limit.
* Pull requests should be tested before submission.
* Any additions in API should be properly documented.
* Avoid unnecessary class coupling where possible.
* Don't include IDE-specific files, class files, jar files, and whatnot; that is what the .gitignore is for!
