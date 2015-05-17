# DMelt 
The core jhplot library of the DMelt project

This library is a core of the <a href="http://jwork.org/dmelt/">DMelt</a> - a free mathematics software for numeric computation, statistics, symbolic calculations, data analysis and data visualization.
It links various components of the DataMelt libraries maintained by the jWork.ORG.


How to compile:

To compile the jhplot package into jhplot.jar, you need to download DataMelt 
from the <a href="http://jwork.org/dmelt/">DataMelt</a> web site and extract the "lib" directory. 
Put this directory inside jhplot directory (where abuild.properties is located)


1) wget -O dmelt.zip http://sourceforge.net/projects/dmelt/files/latest/download
   unzip dmelt.zip;    


2) Assuming you have "lib" directory, build the project:

   ant
   ant run  

The last command  runs a simple Java example.


3) Now you can move the created file under DMelt location:

  cp jhplot.jar lib/system/
  
and you can start the DMelt as dmelt.sh (or dmelt.bat)


S.Chekanov
DMelt (http://jwork.org/dmelt/) creator.

