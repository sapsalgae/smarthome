import os
import sys
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))

import ControlDoor

class Executer:
	def __init__(self, tcpServer):
		self.andRaspTCP = tcpServer
	def startCommand(self, command):
		if command == "1\n":
			unlockDoor()
			print "unlock"
			self.andRaspTCP.sendAll("unlock\n")
		elif command == "0\n":
			lockDoor()
			print "lock"
			self.andRaspTCP.sendAll("lock\n")
