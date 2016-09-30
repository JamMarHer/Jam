# Mushroom
Is an UI that helps testing potential invariants in ROS systems by falsifying their properties. This program depends on a project that can
be found here (https://CCCLXIX@bitbucket.org/afsafzal/daikon-ext.git) checkout to branch Marcos. 

## Requirements
  1. Follow the instructions in the daikon-ext repository, which contains files necessary for Mushroom to run correctly. 
  
  2. After instrumenting and recording the system potential invariants, run Daikon on the trace_translation output and then run 
    the post-daikon filter. We need the post-daikon filter to test the system. 
    
  3. If you had followed the instructions correctly you have modified some ROS files. Mushroom verifies that the system is unmodified in case
it is then it reverse the ROS to its original state in order to mimic a more realistic scenario.

## Instructions
  1. We recommend running Mushroom with intellij (the project is being developed using intellij) after cloning the project, run the program.
  
  2. The program itself guides you through the setup. 
    * Locating our project repository (daikon-ext)
    * Locating Daikon. (Mushroom doesn't currently use Daikon but it will in the future)
    
  3. Look in the menu bar the option Tests > Architectural Invariant Test. A window is going to ask you to provide the post-daikon
  filtered file. 
  
  4. After providing the file, Mushroom is going to analize the input and will provide you with a more graphical view of the report.
  In order to initialize the test, we need to provide Mushroom with the project path (ros_catkin_ws, catkin_ws, etc) and also the 
  .launch or script to run (Note that Mushroom still missing c support (not for long)). 
  
  5. Click the start button, Mushroom will ask you if you want to save the system if is ran correctly (for convience), then it will
  ask you for the ros package that you want to run. Finally it would ask you for commands that the system may depend on.
  
  6. Currently Mushroom will start breaking the all the potential invariants presented in the report, the goal is to have test cases for each potential invariant, that is the projects next goal.
  that is broken.
