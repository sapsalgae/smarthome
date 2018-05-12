# motor control

import RPi.GPIO as GPIO
import time

pin = 17
GPIO.setmode(GPIO.BCM)
GPIO.setup(pin, GPIO.OUT)

p = GPIO.PWM(pin, 50)
p.start(0)

lock = 5
unlock = 25

def unlockDoor(self):
	p.ChangeDutyCycle(25)

def lockDoor(self):
	p.ChangeDutyCycle(5)
