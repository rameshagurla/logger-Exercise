# Simple Logger
A simple but useful Java logger to use everywhere.

## How to use

Initialization :
```Java
Logger.init("logging.properties", Level.WARNING);
// or
Logger.init("logging.properties");
Logger.setLevel(Level.WARNING);
```

Configuration file as follow (example) :
```
# Parameters for good behavior
handlers=java.util.logging.ConsoleHandler
.level=WARNING
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter

# Customize date/time shown with this line
java.util.logging.SimpleFormatter.format=[%1$tF %1$tT][%4$s]%5$s %n

# Specify your app name here
app_name=YourApp
# (Optional, specify your app package)
default_package=com.your.app
# (Optional, specify an output file pattern)
output_file=output.log
```

## Maven

You can use this project as a maven dependency with this :
```XML
<dependency>
    <groupId>com.github.rameshagurla</groupId>
    <artifactId>simple-logger</artifactId>
    <version>1.3.1</version>
</dependency>
```
