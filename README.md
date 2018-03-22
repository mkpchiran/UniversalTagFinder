# Universal Tag Finder

1) tagfinder-1.0.0.jar

A simple command line app to query XHTML content using jsoup.

To build 
./gradlew clean build

To run
java -jar build/libs/tagfinder-1.0.0.jar -p [directory path] -q [css query]

output

file name : element
file name  has  1 elements
directory has  282 elements
Jul 14, 2017 11:51:10 AM parser.Cli parse
INFO: Using cli argument -p=directory
Jul 14, 2017 11:51:10 AM parser.Cli parse
INFO: Using cli argument -q=query

usage: Main
 -h,--help              show help.
 -o,--outer <arg>       outer html off
 -p,--directory <arg>   Directory Path
 -q,--query <arg>       css query
                        for more info about css selectors visit
                        https://www.w3schools.com/cssref/css_selectors.asp
 -r,--result <arg>      get result print on off
 -t,--type <arg>        set query type text , comments or default xhtml

2) tagfinderui-1.0.0.jar

A simple jfx app to query XHTML content using jsoup.

To build 
cd tagfinderui
./gradlew clean build

To run
java -jar build/libs/tagfinderui-1.0.0.jar 

Also if need to build the native app.

./gradlew jfxnative

App will be at build/jfx/native