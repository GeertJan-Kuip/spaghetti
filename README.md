## About this project

_*Note: this was my first project, it doesn't run properly outside my own Intellij environment.*_

This is a pet project that I developed to learn Java. It reads a Java codebase, analyses it and draws a network graph. The nodes in this graph represent the classes and interfaces found in the code, the lines represent invocations of one class of another class.

### Getting started

To get started with the program, go to File->Load Directory and select a folder containing a Java project. When loading is done, select Graphics->Draw Network Graph to create a 'spaghetti' drawing that is based on the code. The nodes of the graph can be dragged to untangle the spaghetti. By clicking on a node you will see its code in the upper right panel. Clicking on the arrow on the middle of the connecting line will give you the code of the two objects connected by that line.

### Caveats

The only files taken into account are files with a .java extension. I have tried to do proper exception handling and bug fixing but probably not good enough yet. The graph drawing has only been tested for smaller projects, it scales up and down but there is not a good zoom function yet.

### Under the hood

The interface is built using the Swing library. The java code is stored as tokens in a SQLite database 'mydb.db.' The network graph is drawn with a so-called 'force directed graph.' Herefore I translated JavaScript code created by Jerome Paddick (https://editor.p5js.org/JeromePaddick/sketches/bjA_UOPip).

### What I learned

- How to make a Swing interface
- The ins and outs of the Swing Event Dispatch Thread
- DefaultStyledDocument, Model-View-Controller principle
- Tokenizing text files
- FileReader, BufferedReader, Files.walk
- How to distribute a simple logger
- Using a SQLite database
- Writing more extensive database requests
- Understanding the sql ResultSet object
- Creating animations using Graph2D object and paintComponent
- The difference between a set and a list
- A whole lot of syntax

### How you can help

I love to learn so if you have checked this project, please let me know what you think. The internet provides tons of help with solving the many smal problems I encountered, but it cannot reflect on the project as a whole so if you find time to do so, I would be extremely grateful.

### What's next

Probably not much with regards to this project. I will start a new one in which networking will be important.



