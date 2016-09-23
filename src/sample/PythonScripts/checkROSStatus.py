#!/usr/bin/env python
import rospy
import os
import sys
import rosservice
import xmlrpclib
import subprocess
import re
import time
import filecmp


def startCheck(projectPath, pythonPath, cPath,task,sudostate):
	absoluteSourceNonModPPath = projectPath + '/src/sample/ROSFiles/tcpros_service.py'
	absoluteSourceNonModCPath = projectPath + '/src/sample/ROSFiles/node_handle.h'
	absoluteSourceModPPath = projectPath + '/src/sample/ROSFilesMod/tcpros_service.py'
	absoluteSourceModCPath = projectPath + '/src/sample/ROSFilesMod/node_handle.h'
	if(task == 'VERSE'):
		if(filecmp.cmp((pythonPath+'tcpros_service.py'), absoluteSourceNonModPPath)):
			print('NON_MOD_ROS')
			sys.exit()
		else:
			if(sudostate == "NON_SUDO"):
				print("ERROR_NON_SUDO")
				sys.exit()
			elif(sudostate == "SUDO"):
				cpP = subprocess.Popen("cp "+absoluteSourceNonModPPath+" "+ pythonPath, shell=True, stdout=subprocess.PIPE).stdout.read()
				cpC = subprocess.Popen("cp "+absoluteSourceNonModCPath+" "+ cPath, shell=True, stdout=subprocess.PIPE).stdout.read()
				if(filecmp.cmp((pythonPath+'tcpros_service.py'),absoluteSourceNonModPPath) and filecmp.cmp((cPath+'node_handle.h'), absoluteSourceNonModCPath)):
					print('SUCCESS')
				else:
					print('ERROR_CP_NON_FAIL')
			sys.exit()
	elif(task == 'REVERSE'):
		if(filecmp.cmp((pythonPath+'tcpros_service.py'),absoluteSourceModPPath) and filecmp.cmp((cPath+'node_handle.h'), absoluteSourceModCPath)):
			print('MOD_ROS')
			sys.exit()
		else:
			if(sudostate == "NON_SUDO"):
				print("ERROR_NON_SUDO")
				sys.exit()
			elif(sudostate == "SUDO"):
				cpP = subprocess.Popen("sudo cp "+absoluteSourceModPPath+" "+ pythonPath, shell=True, stdout=subprocess.PIPE).stdout.read()
				cpC = subprocess.Popen("sudo cp "+absoluteSourceModCPath+" "+ cPath, shell=True, stdout=subprocess.PIPE).stdout.read()
				if(filecmp.cmp((pythonPath+'tcpros_service.py'),absoluteSourceModPPath) and filecmp.cmp((cPath+'node_handle.h'), absoluteSourceModCPath)):
					print('MOD_SUCCESS')
				else:
					print('ERROR_CP_MOD_DAIL')


if __name__ == '__main__':
	if len(sys.argv) < 2:
		print('ERROR_NO_SOURCE_PATH')
		sys.exit()
	elif len(sys.argv) < 3:
		print("ERROR_NO_PPATH")
		sys.exit()
	elif len(sys.argv) < 4:
		print("ERROR_NO_CPATH")
		sys.exit()
	elif len(sys.argv) < 5:
		print("ERROR_NO_TASK")
		sys.exit();
	elif len(sys.argv) < 6:
		print("ERROR_NO_SUDO_STATE")
		sys.exit()
	else:
		startCheck(sys.argv[1], sys.argv[2],sys.argv[3],sys.argv[4], sys.argv[5])
		
