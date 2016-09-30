# Mushroom
Is an UI that helps testing potential invariants in ROS systems by falsifying their proteties. This program depends on a project that can
be found here (https://CCCLXIX@bitbucket.org/afsafzal/daikon-ext.git) checkout to branch Marcos. 

## Requirements
  1) Follow the instructions in the daikon-ext repository, which contains files necessary for Mushroom to run correctly. 
  -2) After instrumenting and recording the system potential invriants, run Daikon on the trace_translation output and then run 
the post-daikon filter. We need the post-daikon filter to test the system. 
  3) If you had followed the instructions correctly you have modified some ROS files. Mushroom verifies that the system is unmodified in case
it is then it reverse the ROS to its original state in order to mimic a more realistic scenario.

## Instructions
1) We recommend running Mushroom from intellij (the project is being developed using intellij) after cloning the project, run the program.
