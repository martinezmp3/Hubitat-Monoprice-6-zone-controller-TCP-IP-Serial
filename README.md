Hubitat
Hubitat Drivers Driver for Hubitat Elevation Hub

(this is a work in progress any feed back will be really appreciate)

What you need:

A Hubitat Hub
A Raspberry Pi (with Ethernet connectivity and in the same network of your hub)
A USB to Serial cable Instalation instructions
Install Raspbian OS any version (https://www.raspberrypi.org/documentation/installation/installing-images/)
If you want headless “no keyboard, mouse or screen” (https://www.raspberrypi.org/documentation/configuration/wireless/headless.md)
Connect USB to Serial from Raspberry Pi to the amp and power on your Raspberry pi
Get access to your console via ssh I use putty but any will work
Install python pip “sudo apt-get install python-pip”
Install pyserial “pip install pyserial”
Create script “nano server.py” (if you are using any other than yours home directory, you need to be root “sudo nano server.py”)
Copy server.py and paste it, on your scrip Ctrl+a and Ctrl+c “https://raw.githubusercontent.com/martinezmp3/Hubitat-Monoprice-6-zone-controler/master/server.py”
Change permits on server.py “chmod +x server.py” (if you are using any other than yours home directory you need to be root “sudo chmod +x server.py”)
By now, you should be able to do “./server.py” or “python server.py” Let it running
Open up the MonoPrice-6-Zone-Amp-Controller.groovy and copy Ctrl+a and Ctrl+c (https://raw.githubusercontent.com/martinezmp3/Hubitat-Monoprice-6-zone-controler/master/MonoPrice-6-Zone-Amp-Controller.groovy)
In your hub web page, select the "Drivers Code" section and then click the "+ New Driver" button
Click in the editor window. Then PASTE all of the code you copied in the previous step.
Click the SAVE button in the editor window
In the hubitat web page, select the "Devices" section, and then click the "Add Virtual Device" button in the top right corner.
In the window that appears, please fill in the "Device Name", "Device Label", and "Device Network Id" fields. Make sure the Device Network Id field is UNIQUE! Could be the same that the devise name or label but HAVE TO BE UNIQUE no other device in you hub could have the same Network Id you will have to set one
In the type filed look for MonoPrice 6 Zone Amp Controller
Click save Device
In the Preferences section ip = ip of the Raspberry Pi, port = port in the scrip (unless you changed should be 10022), Number of amp is if you have more than one amp (not implemented yet), Zone = zone you want to control (you have to create one virtual device per zone, Percent to dec/enc = (not implemented yet)
Click save
If everything went well you should be able to control the zone now (this is a work in progress any feed back will be really appreciate)
