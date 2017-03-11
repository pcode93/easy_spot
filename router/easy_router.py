
import sys
import datetime
import binascii
import httplib
import json
import parse_layer
import re

from bluepy.btle import Peripheral
from bluepy.btle import Service
from bluepy.btle import Characteristic
from bluepy.btle import UUID
from bluepy.btle import DefaultDelegate
from bluepy.btle import Descriptor
from bluepy.btle import ADDR_TYPE_RANDOM as random
from bluepy.btle import BTLEException
from threading import Thread

class BluetoothResource():
	def __init__(self,char,type):
		self.visible = True
		self.characteristic = char
		self.descriptor = self.characteristic.getHandle() + 1
		self.type = type
		
class NotifyDelegate(DefaultDelegate):
	def __init__(self,resources, id):
		DefaultDelegate.__init__(self)
		self.resources = resources
		self.id = id
	
	def handleNotification(self, cHandle, data):
		for res in self.resources:
			if(res.characteristic.getHandle() == cHandle):
				print ord(data)
				print data
				parse_layer.update_spot('parking1', self.id, ord(data))
				break

class NotifyThread(Thread):
	def __init__(self,peripheral):
		Thread.__init__(self)
		self.peripheral = peripheral
		for rsc in  self.peripheral.resources:
			if "NOTIFY" in rsc.type:
				self.peripheral.device.writeCharacteristic(rsc.descriptor,b'\x01\x00', False)
				
	def run(self):
		while True:
			if self.peripheral.device.waitForNotifications(1.0):
				continue
			
class PeripheralResource():
    	def __init__(self,peripheral,id):
		self.visible = True
		self.device = Peripheral(peripheral,random)
		services = self.device.getServices()
		self.resources = []
		for service in services:
			try:
				for char in service.getCharacteristics():
					btRes = BluetoothResource(char = char, type = char.propertiesToString())
					self.resources.append(btRes)
					if char.uuid != UUID(0x2A00) and "WRITE" in char.propertiesToString():
						self.led_indicator = char
			except BTLEException as btexc:
				if(btexc.code == 2):
					continue
		self.device.setDelegate(NotifyDelegate(self.resources,id))
		notifyThread = NotifyThread(self)
		notifyThread.start()

def push_callback(notification):
		print notification
		m = re.search('\"(\d+)-(.+)\",', notification)
		if m.group(2) == 'zarezerwowane':
			peripherals[int(m.group(1))].led_indicator.write(b'\x01')

peripherals = {}
parse_layer.start_parse(push_callback)
parse_layer.register_device('parking1')
dev = PeripheralResource(sys.argv[1], '1')
peripherals[1] = dev
k = input("")
