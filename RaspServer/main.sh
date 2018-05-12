#! switch with camera
gpio -g mode 18 input
gpio -g mode 18 up

gpio -g mode 23 output
gpio -g write 23 0

on=1
off=0
push=0

led_active='gpio -g write'
led_array="23"

{
	while [ True ]
	do
		sw=`gpio -g read 18`

		if [ $sw -eq $push ];then
			echo "camera on"
			echo `sh mjpg-streamer/mjpg-streamer-experimental/mjpg.sh`
		fi
	done
}&

python PySocket/main.py
