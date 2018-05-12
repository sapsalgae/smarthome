import socket, threading

class TCPServerThread(threading.Thread):
	def __init__(self, commandQueue, tcpServerThreads, connections, connection, clientAddress):
		threading.Thread.__init__(self)

		self.commandQueue = commandQueue
		self.tcpServerThreads = tcpServerThreads
		self.connections = connections
		self.connection = connection
		self.clientAddress = clientAddress

	def run(self):
		a = True
		try:
			while a:
				data = self.connection.recv(1024).decode()

				if not data:
					print 'tcp server :: exit :', self.connection

				print 'tcp server :: client :', data
				self.commandQueue.put(data)

		except Exception:
			print "tcpServerThread Error"
			self.connections.remove(self.connection)
			self.tcpServerThreads.remove(self)
			exit(0)

		self.conenctions.remove(self.connection)
		self.tcpServerThreads.remove(self)

	def send(self, message):
		print 'tcp server :: ', message

		try:
			for i in range(len(self.connections)):
				self.connections[i].sendall(message.encode())
		except Exception:
			print "Sending Error"

