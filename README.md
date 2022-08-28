# Text Editor
## By Andre Vrey and Joel Yap (21007112)

### Description
The program has five main tabs, File, Search, View and Help are functional. 

File has the New, Open, Save, Exit and Print menu items.\
New opens a dialog window to clear all editor pane text with or without saving.\
Open opens a dialog window to select and open a text or ODT file.\
Save opens a dialog window to save text files in a chosen location.\
Print opens a window to print the editor text content or save as PDF.\
Convert to PDF converts the text to a PDF window where it can be edited or saved.\
Exit closes the current frame.

Search opens a dialog window that allows the user to enter a case-sensitive word and jump to the matching word in the editor pane.\
View has a Display Date And Time function which adds the current date and time to the top of the existing text.\
Help has an About function that shows basic information about the Text Editor.

### YAML
The Text Editor uses a config1.yml file to control the bounds of the starting Jframe. The first two Size values are the x and y values for the frame's starting position on the screen, and the second two are the width and height for the frame's size. The position and size of the frame can be modified by changing these four values in the YAML file.

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
bfe4729 Done: Added Convert to PDF method to write the text to a PDF file
