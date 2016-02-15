#A processing.org library to create transparent windows#

With Ghost you can create transparent windows, well not really, Ghost displays an image of your desktop, so it looks like the window is transparent.

There are three different modes available:

       Ghost ghost;

       // transparent fullscreen window
       ghost = new FullscreenGhost(this)
       
       // transparent window at position x:0, y:100, width: 200, height: 300
       ghost = new WindowedGhost(this, 0, 100, 200, 300);
       
       // transparent window, which sticks to the screen boarder  
       // ("top", "right", "bottom", "left"), third parameter is window width / height
       ghost = new StickyGhost(this, "top", 100);
       
     }
     
Tested on Windows 8 and Mac OSX 10.7.5 Lion with Processing 2.0b6

Please not that you need the **newest Processing beta** for this to run! 

##Compiler notes##

The AWTUtilities class is only available in Java 1.6_10+ and needs some special setup in Eclipse. Follow this tutorial to get rid of the error message: 
[Access restriction: Class is not accessible due to restriction on required library](http://www.digizol.com/2008/09/eclipse-access-restriction-on-library.html)
