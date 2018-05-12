import tcpServer
import executer
import Queue
import time

IP = "192.168.10.105"
PORT = 8090
commandQueue = Queue.Queue()

andRaspTCP = tcpServer.TCPServer(commandQueue, IP, PORT)
andRaspTCP.start()
	 
commandExecuter = executer.Executer(andRaspTCP)
	 
while True:
	try:
		command = commandQueue.get()
		commandExecuter.startCommand(command)
	except Exception:
		print "main.py Error"
		pass
				 


