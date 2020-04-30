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
		}
	}
}
def setChildzones(){
	def children = getChildDevices()
	children.each {child->
		child.setZone()
	}
}
def recreateChildDevices() {
    log.debug "Parent recreateChildDevices"
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
	log.debug "Parent deleteChildren"
	def children = getChildDevices()
    children.each {child->
  		deleteChildDevice(child.deviceNetworkId)
    }
}
def CloseTelnet(){
telnetClose() 
}
def installed() {
	log.info('Parent MonoPrice 6 Zone Amp Controller: installed()')
	createChildDevices()
	initialize()
}
def updated(){
	log.info('Parent MonoPrice 6 Zone Amp Controller: updated()')
	initialize()
	unschedule()
	runEvery1Minute(pollSchedule)
	recreateChildDevices()
}
def pollSchedule(){
    forcePoll()
}
def initialize(){
	log.info('Parent MonoPrice 6 Zone Amp Controller: initialize()')
	telnetClose() 
	//
	telnetConnect([termChars:[13]], settings.IP, settings.port as int, '', '')
//	telnetConnect([termChars:[11,12]], settings.IP, settings.port as int, '', '')
//	telnetConnect([terminalType: 'VT100'], settings.IP, settings.port as int, '', '')

}
def forcePoll(){
	log.debug "Polling"
	sendMsg("?10")
}
def poll(){
	forcePoll()
/*    if(now() - state.lastPoll > (60000))
        forcePoll()
    else
        log.debug "poll called before interval threshold was reached"
*/
}
def sendMsg(String msg){
	
	log.info("Sending telnet msg: " + msg)
	return new hubitat.device.HubAction(msg, hubitat.device.Protocol.TELNET)
}
private parse(String msg) {
	log.debug("Parse: " + msg)
	
	if (!(msg.contains("Command Error")) && (msg.length()>5))
	{
	def children = getChildDevices()
	children.each {child->
		if (msg.substring(3,5).toInteger() == child.currentValue("zone")){
			child.UpdateData (msg)
//			log.debug("found mach: "+ msg)
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
