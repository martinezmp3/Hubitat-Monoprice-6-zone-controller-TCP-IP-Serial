# Hubitat-Monoprice-6-zone-controller-TCP-IP-Serial
Hubitat Drivers Driver for Hubitat Elevation Hub
(This is a work in progress any feedback will be really appreciate)

	What you need:
	
-	A Hubitat Hub
-	A USR-TCP232-302 Low-Cost RS232 Serial to Ethernet Converter
-	DB9 to DB9 RS232 Serial Cable Male to Male

	Instalation instructions:
	
-	Connect RS232 Serial to Ethernet to the switch or router on the same network where your habitat hub and pc you are using to configure the device is
-	Connect RS232 Serial to Ethernet to the console port on the back of the amp whit DB9 to DB9 RS232 Serial Cable
-	Now you need to find the IP that the device have use the default IP in the one a got was 192.168.0.7 and put the network card of the pc on the same range
-	Log in to the device and go to the local IP Config decide if you want use DHCP (let the router give an IP you much set an static reservation) or you are going to set the IP Static using an IP you know the router is not going to assign to another device. In any case you got to select a IP inside the same range of your hub.
-	Go to Serial Port and set it this way:
	Baud Rate：9600
	Data Size: 8
	Parity: None
	Stop Bits：1
	Local Port Number：23
	Work Mode: TCP Server
	RESET： uncheck
	LINK： check
	INDEX: uncheck
	Similar RFC2217：uncheck
-	Now add drivers to your habitat hub open up https://raw.githubusercontent.com/martinezmp3/Hubitat-Monoprice-6-zone-controller-TCP-IP-Serial/master/Child-MonoPrice-6-Zone-Amp-Controller.groovy
Ctrl+a and Ctrl+c to Copy
-	In your hub web page, select the "Drivers Code" section and then click the "+ New Driver" button
-	Click in the editor window. Then PASTE all of the code you copied in the previous step, click the SAVE button in the editor window
-	Do the same for second driver https://raw.githubusercontent.com/martinezmp3/Hubitat-Monoprice-6-zone-controller-TCP-IP-Serial/master/Parent-MonoPrice-6-Zone-Amp-Controller.groovy
-	Ctrl+a and Ctrl+c to Copy
-	In your hub web page, select the "Drivers Code" section and then click the "+ New Driver" button
-	Click in the editor window. Then PASTE all of the code you copied in the previous step, click the SAVE button in the editor window
-	In the hubitat web page, select the "Devices" section, and then click the "Add Virtual Device" button in the top right corner.
-	In the window that appears, please fill in the "Device Name", "Device Label", and "Device Network Id" fields. Make sure the Device Network Id field is UNIQUE! No other device on rout hub could have the same Network id
-	In the type filed look for “Parent MonoPrice 6 Zone Amp Controller”
-	Click save
-	In the Preferences section IP = IP of RS232 Serial to Ethernet, port = port of RS232 Serial to Ethernet (if you fallow al the instructions so far then 23), Number of amp is if you have more than one amp , Name of zone = is wherever name you have for each zone Livingroom kitchen… the parent device will create a child device per Zone
-	Click save
-	If everything went well in a few seconds you should be able to see all the childs devices and will be able to control them.

Note: status change will come on a 60 second interval till this moment no other way to do it.
(This is a work in progress any feedback will be really appreciate)
