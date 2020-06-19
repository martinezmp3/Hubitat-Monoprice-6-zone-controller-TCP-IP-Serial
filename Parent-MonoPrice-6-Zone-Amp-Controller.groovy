/* 
Parent driver fo Monoprice 6 zone audio 
monoprice.com/product?p_id=10761
This driver is to control the monoprice 6 zone amplifier. 
I wrote this diver for personal use. If you decide to use it, do it at your own risk. 
No guarantee or liability is accepted for damages of any kind. 
for the driver to work it also needs RS232 to Ethernet like this one 
https://www.aliexpress.com/item/32988953549.html?spm=a2g0o.productlist.0.0.517f5e27r8pql4&algo_pvid=f21f7b9e-0d3b-4920-983c-d9df0da59484&algo_expid=f21f7b9e-0d3b-4920-983c-d9df0da59484-1&btsid=0ab6f83115925263810321337e7408&ws_ab_test=searchweb0_0,searchweb201602_,searchweb201603_
https://www.amazon.com/USR-TCP232-302-Serial-Ethernet-Converter-Support/dp/B01GPGPEBM/ref=sr_1_6?dchild=1&keywords=RS232+to+Ethernet&qid=1592526464&sr=8-6
Jorge Martinez
*/

metadata {
	definition (name: "Parent MonoPrice 6 Zone Amp Controller", namespace: "jorge.martinez", author: "Jorge Martinez"){
		capability "Polling"
		capability "Telnet"
		capability "Initialize"
//		capability "Actuator"
//		capability "Switch"
//      capability "Sensor"
//		capability "AudioVolume"
		command "recreateChildDevices"
    	command "poll"
		command "forcePoll"
		command "sendMsg" , ["STRING"]
		command "CloseTelnet"
		command "setChildzones"
		command "Unschedule"
	}
	preferences {
		section("Device Settings:") 
		{
			input "IP", "String", title:"IP of Amp Controller", description: "", required: true, displayDuringSetup: true
			input "port", "NUMBER", title:"port of Amp Controller", description: "", required: true, displayDuringSetup: true
			input "Zone1Name", "String", title:"Name Of Zone 1", description: "", required: true, defaultValue: "Zone_1"
			input "Zone2Name", "String", title:"Name Of Zone 2", description: "", required: true, defaultValue: "Zone_2"
			input "Zone3Name", "String", title:"Name Of Zone 3", description: "", required: true, defaultValue: "Zone_3"
			input "Zone4Name", "String", title:"Name Of Zone 4", description: "", required: true, defaultValue: "Zone_4"
			input "Zone5Name", "String", title:"Name Of Zone 5", description: "", required: true, defaultValue: "Zone_5"
			input "Zone6Name", "String", title:"Name Of Zone 6", description: "", required: true, defaultValue: "Zone_6"
			input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
			input name: "NumberAmps", type: "enum", description: "", title: "Number Amps", options: [[1:"1"],[2:"2"],[3:"3"]], defaultValue: 1
			input name: "PollSchedule", type: "enum", description: "", title: "Poll frequency in min", options: [[1:"1"],[2:"5"],[3:"15"],[4:"30"]], defaultValue: 1
			// 1, 5, 15 and 30 minites
		}
	}
}
def Unschedule(){
	if (logEnable) log.debug "Parent unschedule"
	unschedule()
}
def setChildzones(){
	if (logEnable) log.debug "Parent setChildzones"
	def children = getChildDevices()
	children.each {child->
		child.setZone()
	}
}
def recreateChildDevices() {
	if (logEnable) log.debug "Parent recreateChildDevices"
    deleteChildren()
    createChildDevices()
}
def createChildDevices() {
	log.debug "Parent createChildDevices"
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-11", [name: "child-${Zone1Name}", label: "${settings.Zone1Name}", zone: 11, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-12", [name: "child-${Zone2Name}", label: "${settings.Zone2Name}", zone: 12, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-13", [name: "child-${Zone3Name}", label: "${settings.Zone3Name}", zone: 13, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-14", [name: "child-${Zone4Name}", label: "${settings.Zone4Name}", zone: 14, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-15", [name: "child-${Zone5Name}", label: "${settings.Zone5Name}", zone: 15, isComponent: false])
	addChildDevice("jorge.martinez","Child MonoPrice 6 Zone Amp Controller", "MP6ZA-child-16", [name: "child-${Zone6Name}", label: "${settings.Zone6Name}", zone: 16, isComponent: false])
	setChildzones ()
}
def deleteChildren() {
	if (logEnable) log.debug "Parent deleteChildren"
	def children = getChildDevices()
    children.each {child->
  		deleteChildDevice(child.deviceNetworkId)
    }
}
def CloseTelnet(){
    telnetClose() 
	unschedule()
}
def installed() {
	log.info('Parent MonoPrice 6 Zone Amp Controller: installed()')
	createChildDevices()
	initialize()
}
def updated(){
	log.info('Parent MonoPrice 6 Zone Amp Controller: updated()')
	initialize()
	//recreateChildDevices()
}
def pollSchedule(){
    forcePoll()
}
def initialize(){
	log.info('Parent MonoPrice 6 Zone Amp Controller: initialize()')
	telnetClose() 
	telnetConnect([termChars:[13]], settings.IP, settings.port as int, '', '')
    unschedule()
	switch (settings.PollSchedule) {
        case "1": runEvery1Minute(pollSchedule);log.info('pollSchedule 1 minute'); break;
        case "2": runEvery5Minutes(pollSchedule);log.info('pollSchedule 5 minute'); break;
        case "3": runEvery15Minutes(pollSchedule);log.info('pollSchedule 15 minute'); break;
		case "4": runEvery30Minutes(pollSchedule);log.info('pollSchedule 30 minute'); break;
        default: log.info('pollSchedule ERROR');
	}
	forcePoll()
}
def forcePoll(){
	if (logEnable) log.debug "Polling"
	sendMsg("?10")
}
def poll(){forcePoll()}

def sendMsg(String msg){
	log.info("Sending telnet msg: " + msg)
	return new hubitat.device.HubAction(msg, hubitat.device.Protocol.TELNET)
}
private parse(String msg) {
	if (logEnable) log.debug("Parse recive: " + msg)
	//if (!(msg.contains("Command Error")) && (msg.length()>5) && (msg.startsWith("#>"))){
    if (msg.substring(1,3)==("#>")) {
        def children = getChildDevices()
	    children.each {child->
		    if (msg.substring(3,5).toInteger() == child.currentValue("zone")){
			    child.UpdateData (msg)
			    if (logEnable) log.debug ("found mach: "+ msg)
		}
	  }
	}
}
def telnetStatus(String status){
	log.warn "telnetStatus: error: " + status
	if (status != "receive error: Stream is closed")
	{
		log.error "Connection was dropped."
		initialize()
	} 
}
