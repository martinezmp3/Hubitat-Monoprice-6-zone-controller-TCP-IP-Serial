/* 
Child driver fo Monoprice 6 zone audio 
monoprice.com/product?p_id=10761
This driver is to control the monoprice 6 zone amplifier. 
I wrote this diver for personal use. If you decide to use it, do it at your own risk. 
No guarantee or liability is accepted for damages of any kind. 
for the driver to work it also needs RS232 to Ethernet like this one:
        https://www.aliexpress.com/item/32988953549.html?spm=a2g0o.productlist.0.0.517f5e27r8pql4&algo_pvid=f21f7b9e-0d3b-4920-983c-d9df0da59484&algo_expid=f21f7b9e-0d3b-4920-983c-d9df0da59484-1&btsid=0ab6f83115925263810321337e7408&ws_ab_test=searchweb0_0,searchweb201602_,searchweb201603_
        https://www.amazon.com/USR-TCP232-302-Serial-Ethernet-Converter-Support/dp/B01GPGPEBM/ref=sr_1_6?dchild=1&keywords=RS232+to+Ethernet&qid=1592526464&sr=8-6 or similar 
        is been test it too on:
        https://www.amazon.ca/gp/product/B087J9F6LF/ref=ppx_yo_dt_b_asin_title_o02_s00?ie=UTF8&psc=1
        08/11/2020
        this driver also work on a rasberry pi running ser2net.py by Pavel Revak https://github.com/pavelrevak/ser2tcp
        is recomended to daemonize the scrips instruction on the gihub https://github.com/martinezmp3/Hubitat-Monoprice-6-zone-controller/blob/master/README.md
        08/12/2020 
        compatibility with 2 and 3 amps connected as a chain i dont own a second amp
        08/20/2020 
        Parent save source name to be display on dashboard
        08/26/2020
        add mute override option
Jorge Martinez
*/
metadata {
    definition (name: "Child MonoPrice 6 Zone Amp Controller", namespace: "jorge.martinez", author: "Jorge Martinez") {
    capability "Switch"
	capability "Initialize"
	capability "Actuator"
    capability "Switch"
    capability "Sensor"
	capability "AudioVolume"
	capability "MusicPlayer"
	attribute "zone" , "NUMBER"
	attribute "mediaSource", "NUMBER"
	attribute "level" , "NUMBER"
	attribute "balance" , "NUMBER"
	attribute "bass" , "NUMBER"
	attribute "treble" , "NUMBER"
    attribute "internalLevel", "NUMBER"
    attribute "trackDescription", "STRING" // Andy @Cobra  https://community.hubitat.com/u/cobra
	command "Source1"
	command "Source2"
	command "Source3"
	command "Source4"
	command "Source5"
	command "Source6"
//	command "setLevel"  ,["NUMBER"]
	command "setZone"
	command "nextTrack"
	command "previousTrack"
    command "toggle"
//  command "KeypadOff"
    }
	preferences {
		section("Device Settings:"){
				input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
	    		input name: "Percent",type: "enum", description: "", title: "Percent to dec/enc", options: [[1:1],[2:2],[3:3],[5:5],[10:10],[15:15]], defaultValue: 2
				input name: "MaxVolumen", type: "NUMBER", description: "", title: "Max volumen allow", defaultValue: 38
//              input name: "TriggerOn", type: "bool", title: "Any Stting change will trun zone on if zone is off", defaultValue: false
                input name: "MuteTriggerPower", type: "bool", title: "Mute/unmute command Trigger on/off", defaultValue: false
			}
	}
		
}
def KeypadOff(){
    try{
//        off ()
//        On ()
    }
    catch (Exception e) {
        		log.warn "can't ${e.message}"
    		}
}
def play (){
    if (logEnable) log.debug "play"
    state.status = "play"
    sendEvent(name: "status", value: "play", isStateChange: true)
}
def stop (){
    if (logEnable) log.debug "stop"
    state.status = "stop"
    sendEvent(name: "status", value: "stop", isStateChange: true)
}
def UpdateData (NewData){
    log.debug NewData
def power = NewData.substring(7,9)
	if (power.toInteger()){
		state.switch = "on"
//        state.status = "on"
//        sendEvent(name: "status", value: "on", isStateChange: true)
		sendEvent(name: "switch", value: "on", isStateChange: true)
		}
	if (!power.toInteger()){
		state.switch = "off"
//        state.status = "off"
//        sendEvent(name: "status", value: "off", isStateChange: true)
    	sendEvent(name: "switch", value: "off", isStateChange: true)
		}
	
def mute = NewData.substring(9,11)
	if (mute.toInteger()){
		state.mute = "muted"
		sendEvent(name: "mute", value: "muted", isStateChange: true)
		}
	if (!mute.toInteger()){
		state.mute = "unmuted"
		sendEvent(name: "mute", value: "unmuted", isStateChange: true)
		}
def vol = NewData.substring(13,15)
	if (state.internalLevel != vol){
		state.internalLevel = vol.toInteger()
		sendEvent(name: "internalLevel", value: state.internalLevel.toInteger(), isStateChange: true)
        state.level = Math.round(state.internalLevel*100/settings.MaxVolumen)
        sendEvent(name: "level", value: state.level, isStateChange: true)
        state.volume  = state.level
        sendEvent(name: "volume", value: state.volume, isStateChange: true)
		}
def source = NewData.substring(21,23)
	if (state.mediaSource != source){
		state.mediaSource = source.toInteger()
		sendEvent(name: "mediaSource", value: state.mediaSource.toInteger(), isStateChange: true)
        state.trackDescription = parent.getChanelName(state.mediaSource.toInteger())
        sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)
		}
def Balance = NewData.substring(19,21)
	if (state.balance != Balance){
		state.balance = Balance.toInteger()
		sendEvent(name: "balance", value: state.balance.toInteger(), isStateChange: true)
		}
def Bass = NewData.substring(17,19)
	if (state.bass != Bass){
		state.bass = Bass.toInteger()
		sendEvent(name: "bass", value: state.bass.toInteger(), isStateChange: true)
		}
def Treble = NewData.substring(15,17)
	if (state.treble != Treble){
		state.treble = Treble.toInteger()
		sendEvent(name: "treble", value: state.treble.toInteger(), isStateChange: true)
		}
	}
def setZone (){
	len = device.deviceNetworkId.length()
	state.zone = device.deviceNetworkId.substring(len-2,len).toInteger()
	sendEvent(name: "zone", value: state.zone, isStateChange: true)
}
def nextTrack(){
/*    if (settings.TriggerOn && state.switch == "off"){
        if (logEnable) log.debug "Truning on"
        on()
    }*/
    if(state.mediaSource<6)
	{
		def newmediaSource = state.mediaSource+1
        if (logEnable) log.debug "next Source${state.mediaSource}"
			try {
			sendEvent(name: "mediaSource", value: (newmediaSource), isStateChange: true)
			state.mediaSource = newmediaSource
            state.trackDescription = parent.getChanelName(newmediaSource)
            sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)
			parent.sendMsg ("<${state.zone}ch0${state.mediaSource}")
  			} 
			catch (Exception e) {
        		log.warn "next Source failed: ${e.message}"
    			}
	}
}
def previousTrack(){
	if(state.mediaSource>1)
	{
/*    if (settings.TriggerOn && state.switch == "off"){
        if (logEnable) log.debug "Truning on"
        on()
    }*/
	def newmediaSource = state.mediaSource-1
        if (logEnable) log.debug "previous Source${state.mediaSource}"
			try {
			    sendEvent(name: "mediaSource", value: (newmediaSource), isStateChange: true)
			    state.mediaSource = newmediaSource
                state.trackDescription = parent.getChanelName(newmediaSource)
                sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)
			    parent.sendMsg ("<${state.zone}ch0${state.mediaSource}")
  			} 
			catch (Exception e) {
        		log.warn "previous Source failed: ${e.message}"
    		}
	}
}
def installed() {
	log.info('MonoPrice 6 Zone Amp Controller: installed() '+state.name + state.zone)
	initialize()
}
def updated(){
	log.info('MonoPrice 6 Zone Amp Controller: updated()')
	initialize()
}
def pollSchedule(){
//    forcePoll()
}
def initialize(){
	log.info('MonoPrice 6 Zone Amp Controller: initialize() '+state.name + state.zone)
}
def setLevel (Percent){
/*    if (settings.TriggerOn && state.switch == "off"){
        if (logEnable) log.debug "Truning on"
        on()
    }*/
	Value = Math.round(Percent/100*settings.MaxVolumen)
    if (logEnable) log.debug ("setLevel call:${Percent}%")
    try {
	    if (Value>settings.MaxVolumen)
	    	Value = settings.MaxVolumen
	    if (Value<0)
	    	Value = 0
        state.volume = Percent
        state.level = Percent
        state.internalLevel = Value
	    sendEvent(name: "volume", value: state.volume, isStateChange: true)
        sendEvent(name: "level", value: state.level, isStateChange: true)
        sendEvent(name: "internalLevel", value: state.internalLevel, isStateChange: true)
		strvolume = "00"
		if (state.internalLevel.toInteger()<10) strvolume = "0${state.internalLevel}"
		else strvolume = state.internalLevel.toString ()
	    parent.sendMsg ("<${state.zone}vo${strvolume}")
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def on() {
    if (logEnable) log.debug "trunning on"
	try {
		state.switch = "on"
        sendEvent(name: "switch", value: "on", isStateChange: true)
        state.trackDescription = parent.getChanelName(state.mediaSource)
        sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)
		parent.sendMsg ("<${state.zone}pr01")
        if (state.mute == muted) unmute()
        }
	 catch (Exception e) {
        log.warn "Call to on failed: ${e.message}"
    }
}
def off() {
    if (logEnable) log.debug "trunning off"
    try {
	 	state.switch = "off"
        sendEvent(name: "switch", value: "off", isStateChange: true) 
        sendEvent(name: "trackDescription", value: "off", isStateChange: true)
        state.trackDescription = "off"
		parent.sendMsg ("<${state.zone}pr00")
        }
	catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def volumeUp() {
    	if (state.volume<100){
		if (logEnable) log.debug "Volumen UP ${state.volume}"
    		try {
	    		def newvolume = (state.volume.toInteger() + settings.Percent.toInteger()).toInteger()
	    		log.debug newvolume
	    		state.volume = newvolume
				sendEvent(name: "volume", value: state.volume.toInteger(), isStateChange: true)
                state.level = state.volume
                sendEvent(name: "level", value: state.level, isStateChange: true)
                Value = Math.round(newvolume/100*settings.MaxVolumen)
                state.internalLevel = Value
                sendEvent(name: "level", value: state.internalLevel, isStateChange: true)
                strvolume = "00"
				if (state.internalLevel.toInteger()<10) strvolume = "0${state.internalLevel}"
				else strvolume = state.internalLevel.toString ()
	   			parent.sendMsg ("<${state.zone}vo${strvolume}")
                } catch (Exception e) {
        		log.warn "Call to off failed: ${e.message}"
    			}
	}
}
def volumeDown() {
    if (state.volume>0){
		if (logEnable) log.debug "Volumen DOWN ${state.volume}"
    		try {
	    		def newvolume = (state.volume.toInteger() - settings.Percent.toInteger()).toInteger()
	    		log.debug newvolume
	    		state.volume = newvolume
				sendEvent(name: "volume", value: state.volume.toInteger(), isStateChange: true)
                state.level = state.volume
                sendEvent(name: "level", value: state.level, isStateChange: true)
                Value = Math.round(newvolume/100*settings.MaxVolumen)
                state.internalLevel = Value
                sendEvent(name: "level", value: state.internalLevel, isStateChange: true)
                strvolume = "00"
				if (state.internalLevel.toInteger()<10) strvolume = "0${state.internalLevel}"
				else strvolume = state.internalLevel.toString ()
	   			parent.sendMsg ("<${state.zone}vo${strvolume}")
                } catch (Exception e) {
        		log.warn "Call to off failed: ${e.message}"
    			}
	}
}
def setVolume(Volume) {
    setLevel (Volume)
}
def mute() {
    if (logEnable) log.debug "mute"
    try {
	    state.mute = "muted"
		sendEvent(name: "mute", value: "muted", isStateChange: true)
        if (MuteTriggerPower){
            if (logEnable) log.debug "Overwite mute power off"
            off()
        }
        if (!MuteTriggerPower) parent.sendMsg ("<${state.zone}mu01")
    } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def unmute() {
    if (logEnable) log.debug "unmute"
    try {
	    state.mute = "unmuted"
		sendEvent(name: "mute", value: "unmuted", isStateChange: true)
        if (MuteTriggerPower){
            if (logEnable) log.debug "Overwite unmute power on"
            on()
        }
        if (!MuteTriggerPower) parent.sendMsg ("<${state.zone}mu00")
    } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def Source1(){
    if (logEnable) log.debug "Source1"
    try {
		state.mediaSource = 1
		sendEvent(name: "mediaSource", value: 1, isStateChange: true)
        state.trackDescription = parent.getChanelName(state.mediaSource)
        sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)
        parent.sendMsg ("<${state.zone}ch0${state.mediaSource}")
    } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def Source2(){
    if (logEnable) log.debug "Source2"
    try {
		state.mediaSource = 2
		sendEvent(name: "mediaSource", value: 2, isStateChange: true)
        state.trackDescription = parent.getChanelName(state.mediaSource)
        sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)
	    parent.sendMsg ("<${state.zone}ch0${state.mediaSource}")
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def Source3(){
    if (logEnable) log.debug "Source3"
    try {	    
		state.mediaSource = 3
		sendEvent(name: "mediaSource", value: 3, isStateChange: true)
        state.trackDescription = parent.getChanelName(state.mediaSource)
        sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)        
		parent.sendMsg ("<${state.zone}ch0${state.mediaSource}") 
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def Source4(){
    if (logEnable) log.debug "Source4"
    try {
		state.mediaSource = 4
		sendEvent(name: "mediaSource", value: 4, isStateChange: true)
        state.trackDescription = parent.getChanelName(state.mediaSource)
        sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)
	    parent.sendMsg ("<${state.zone}ch0${state.mediaSource}")
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def Source5(){
    if (logEnable) log.debug "Source5"
    try {	    
		state.mediaSource = 5
		sendEvent(name: "mediaSource", value: 5, isStateChange: true)
        state.trackDescription = parent.getChanelName(state.mediaSource)
        sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)        
	    parent.sendMsg ("<${state.zone}ch0${state.mediaSource}")
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def Source6(){
    if (logEnable) log.debug "Source6"
    try {
		state.mediaSource = 6
		sendEvent(name: "mediaSource", value: 6, isStateChange: true)
        state.trackDescription = parent.getChanelName(state.mediaSource)
        sendEvent(name: "trackDescription", value: state.trackDescription, isStateChange: true)        
		parent.sendMsg ("<${state.zone}ch0${state.mediaSource}")
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
