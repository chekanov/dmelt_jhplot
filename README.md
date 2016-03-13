# DataMelt (jhplot package)
The core jhplot library of the DMelt project

This library is a core of the <a href="http://jwork.org/dmelt/">DMelt</a> - a free mathematics software for numeric computation, statistics, symbolic calculations, data analysis and data visualization.
It links various components of the DataMelt libraries maintained by the jWork.ORG.

<h2>How to compile</h2>

To compile the jhplot package into jhplot.jar, you need to download DataMelt from the <a href="http://jwork.org/dmelt/">DMelt</a> web site and extract the "lib" directory. Put this directory inside the jhplot directory (where abuild.properties is located). Make sure that the ant buid tool is installed.


1) Get this package:
   wget https://github.com/chekanov/dmelt_jhplot/archive/master.zip
   unzip master.zip

1) Add additional third-party libraries from DMelt:
   <pre>
   wget -O dmelt.zip http://sourceforge.net/projects/dmelt/files/latest/download
   unzip dmelt.zip;
   </pre>
2) Copy these libraries:
   
   cp -rf dmelt/lib dmelt_jhplot-master/

2) Assuming you have the "lib" directory, build the project:

   <pre>
   cd dmelt_jhplot-master/
   ant
   ant run
   </pre>
The last command  runs a simple Java example showing a log scale for a canvas filled with random numbers.

3) Now you can move the created file under DMelt location:

  <pre>
  cp jhplot.jar ../dmelt/lib/system/
  </pre>

and you can start the DMelt as dmelt.sh (or dmelt.bat) 


This version of jhplot is licensed under the GNU public license and included
in the community edition of DMelt. A version with more permissive license is 
available from the DMelt web page.

S.Chekanov
DMelt (http://jwork.org/dmelt/)

