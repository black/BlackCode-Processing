
The Text 2.0 Framework Bundle
=============================


Quick Start Guide
-----------------

1) Install Java 7 (http://java.com/)
2) Start the 'Tracking Server'
    - In case you have a real eye tracker, edit 'config.properties' first 
    - Set ...adapter.id to gazeadapter:tobii
    - Set ...tobii.server to the address of your tracker 
      (e.g., TX120-203-83900194.local.)
    - In case you don't have a tracker, don't change anything, the simulator 
      is used by default
3) Run the 'Diagnosis' to check that things work (and to calibrate when using an 
   eye tracker)

Now the infrastructure should be up and running. Next, have a look at the examples (with 
the latest Google Chrome or Firefox 4 version) or at PEEP. Have fun.

    

Overview
--------
- Eye tracking right in your browser, combined seamlessly with CSS and JavaScript.
- Supports (almost) arbitrary HTML - gaze active image tags rotated using 3D CSS 
  transforms? No problem.
- Runs on Mac OS, Windows and (in theory) on Linux
- Google Chrome >10 and Firefox 4 supported
- Create extensions, write and add your favorite algorithms dynamically
- Record interaction sessions as XML streams and replay them (w. limitations)
- Supports all Tobii trackers using the TET API (e.g., 1750, T60, X120, TX300 ...)
- Mouse fallback and a simulator with configurable accuracy included
- Brain tracking using the Emotiv headset (experimental)
 - Also provides a Java API to access eye tracking data
- Open source (LGPL)



Version 1.4.1 Changelog
-----------------------

This release contains mainly stability and usability enhancements. Especially the 
tracking server and the browser plugin are way more robust now and contain many 
little features that help you to prevent and detect problems. The changes in detail:


*General*
  * Added "Lightning", an intelligent text cursor placement tool
  * JSPF Log Converter added (allows you to convert debug logs into text files to 
    trace errors)
  * New network protocol which is 80% faster 
  * General stability improvements 
  * Code cleanups 


*Browser Plugin*
  * Image URLs are now recorded for the replay
  * Debugging-overlay extension added, can be triggered from JavaScript
  * JavaScript debugging added for browsers which support it
  * Fixation listener now provides more information
  * Chrome 10 and Firefox 4 (and later) should be able to reload the page when using the object tag
  * Simplified extension development, you can now tag methods with @ExtensionMethod, 
    the rest is done automatically. The old interface was moved to DynamicExtension
  * Changed extension internals (e.g., moved singleton services to the InformationBroker)
  * Various bug fixes


*Diagnosis*
  * Starts up faster now in most situations
  * Splash screen added
  * Various bug fixes


*PEEP*
  * Added precision-API for more accurate timing information


*Tracking Server*
  * The server is much more robust now (it even survives reconnecting the tracker's LAN 
    cable and still continues to track afterwards)
  * Lots of debugging information added, the most common problems are now detected automatically
  * Default config changed for improved startup speed of the clients 
  * The server is now able to check for framework-updates and download them for you
  * Tray icon added


This release has been successfully tested with Chrome 14 and Firefox 6.0 on Windows and Mac, 
Java 7.0, Processing 1.2.1. Higher versions usually also work. 





Contact and More Info
---------------------

Visit http://text20.net for the latest infos. There is a technical support forum 
at http://groups.google.com/group/text20. For general information write to 
info@text20.net. The development portal can be found at http://code.text20.net



Copyright and Credits
---------------------

The Text 2.0 Framework is built on top of the EyeTracker2Java library created by Georg 
Buscher and Florian Mittag. Georg was also the person that had the most significant 
influence on the framework's development through his ideas, suggestions and participation, 
let alone his influence on the whole Text 2.0 project.

The framework also includes the hard work of Arman Vartan, Thomas Lottermann, Eugen Massini, 
Andreas Buhl, as well as the suggestions, tips and feedback of many more. Thank you very much!


(c) 2011, Ralf Biedert, German Research Center for Artificial Intelligence (DFKI)




Legal Info and Warranty
-----------------------

NO WARRANTY

THE PROGRAM IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL, 
BUT WITHOUT ANY WARRANTY. IT IS PROVIDED "AS IS" WITHOUT WARRANTY 
OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED 
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE 
OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, 
YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.

IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW THE AUTHOR WILL BE LIABLE 
TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR 
CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE 
PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED 
INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF 
THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF THE AUTHOR HAS 
BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.


