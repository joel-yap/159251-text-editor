# Text Editor
  
## By Andre Vrey (21009282) and Joel Yap (21007112)
  
### Requirements  
  
- JRE https://www.java.com/en/download/manual.jsp
- Internet connection (for 'Read Source Code' function)

### Installation  
  
Repository: https://github.com/159251/251-Assignment1-2022-Andre-Joel  
  
<b>Clone</b>  
HTTPS: https://github.com/159251/251-Assignment1-2022-Andre-Joel.git  
SSH: git@github.com:159251/251-Assignment1-2022-Andre-Joel.git  

<b>Run/Executable</b>  
The executable can be found in the top level directory i.e. 251-Assignment1-2022-Andre-Joel/TextEditor.jar
  
------
  
### Description  
A standard text editor (or text processor) to write text using standard text encoding formats (i.e., ASCII/UTF-8), developed in Java.  
Main menu items:
  
<b>File</b>  
- New: Opens a dialog window to clear all editor pane text with, or without saving.
- Open: Opens a dialog window to select and open a text or ODT file.
- Save: Opens a dialog window to save text files in a chosen location.
- Print: Opens a standard print dialogue window to print the text contained in the editor, or save as PDF.
- Convert to PDF: Converts the text to a PDF window where it can be edited or saved.
- Exit: Exits the program.
  
<b>Search</b>  
- Case Sensitive: Opens a dialog window that allows the user to enter a case-sensitive word and jump to the matching word in the editor pane.
  
<b>View</b>  
- Display Date And Time: Adds the current date and time to the top of the existing text.
  
<b>Manage</b>  
- Read Source Code: Parse and style the source code in the editor window to the selected programming language (Requires an internet connection).

<b>Help</b>
- About: Shows basic version and dev information about the Text Editor.
    
------
  
### YAML
The Text Editor uses a config1.yml file to control the bounds of the starting Jframe. The first two Size values are the x and y values for the frame's starting position on the screen, and the second two are the width and height for the frame's size. The position and size of the frame can be modified by changing these four values in the YAML file.
  
------
  
### Significant Git commits:
Joel:\
16eb86f First commit: Main class, basic StartWindow class with Jframe, menubar, File menu\
62634fe Added Open file function: Open file method to open a text file and read text contents to editorPane\
23ea016 Added Save Feature: Save File method to save contents of editorPane to a new file location selected by user\
03d2192 Added Exit function: Exit method to close the application or prompt save and exit if text is present\
4be14f7 Added Open ODT file: Modified Open file to also open and read .odt files instead of just text files\
78fb4fc Added all menu bar items: Added Time and Date function

Andre:\
2c2635c Added editor pane: Editor class, JEditorPane, New File method to clear editorPane with functional ConfirmDialog for checking unsaved changes\
5ee830f Added Search: Search method to focus on selected text in editorPane, moved nested internal ActionListener methods to external methods\
b12f43d Added Print function: Print method to print editorPane text with printer or PDF\
bfe4729 Added Convert to PDF: Method to write the text to a PDF file\
328cac2 Added Read Source Code: Ability to read source code files .java, .py, .cpp or similar different syntax should be shown in different colours  
