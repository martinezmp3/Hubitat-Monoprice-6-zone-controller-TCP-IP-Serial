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
    }
	preferences {
		section("Device Settings:"){
				input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
	    		input name: "Percent",type: "enum", description: "", title: "Percent to dec/enc", options: [[1:1],[2:2],[3:3],[5:5],[10:10],[15:15]], defaultValue: 2
				input name: "MaxVolumen", type: "NUMBER", description: "", title: "Max volumen allow", defaultValue: 38
			}
	}
		
}
def UpdateData (NewData){
	log.debug NewData

	def power = NewData.substring(7,9)
	if (power.toInteger()){
		state.switch = "on"
		sendEvent(name: "switch", value: "on", isStateChange: true)
		}
	if (!power.toInteger()){
		state.switch = "off"
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
	if (state.volume != vol){
		state.volume = vol.toInteger()
		sendEvent(name: "volume", value: state.volume.toInteger(), isStateChange: true)
		}
	def source = NewData.substring(21,23)
	if (state.mediaSource != source){
		state.mediaSource = source.toInteger()
		sendEvent(name: "mediaSource", value: state.mediaSource.toInteger(), isStateChange: true)
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
	if(state.mediaSource<6)
	{
		def newmediaSource = state.mediaSource+1
		if (logEnable) {
			log.debug "next Source"
			log.debug state.mediaSource
			}
			try {
			sendEvent(name: "mediaSource", value: (newmediaSource), isStateChange: true)
			state.mediaSource = newmediaSource
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
		def newmediaSource = state.mediaSource-1
		if (logEnable) {
			log.debug "previous Source"
			log.debug state.mediaSource
			}
			try {
			sendEvent(name: "mediaSource", value: (newmediaSource), isStateChange: true)
			state.mediaSource = newmediaSource
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
//	unschedule()
//	runEvery1Minute(pollSchedule)
}
def pollSchedule(){
//    forcePoll()
}
def initialize(){
	log.info('MonoPrice 6 Zone Amp Controller: initialize() '+state.name + state.zone)
}
def setLevel (Percent){
	Value = Math.round(Percent/100*settings.MaxVolumen)
    if (logEnable) log.debug Value
    try {
	    if (Value>settings.MaxVolumen)
	    	Value = settings.MaxVolumen
	    if (Value<0)
	    	Value = 0
	    state.volume = Value
	    sendEvent(name: "volume", value: state.volume, isStateChange: true)
		strvolume = "00"
		if (state.volume.toInteger()<10) strvolume = "0${state.volume}"
		else strvolume = state.volume.toString ()
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
		parent.sendMsg ("<${state.zone}pr01")
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
		parent.sendMsg ("<${state.zone}pr00")
        }
	catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def volumeUp() {
	if (state.volume<100){
		if (logEnable) {
			log.debug "Volumen UP ${state.volume}"
			}
    		try {
	    		def newvolume = (state.volume.toInteger() + settings.Percent.toInteger()).toInteger()
	    		log.debug newvolume
			if (newvolume.toInteger()>settings.MaxVolumen.toInteger()) //ovewrite volume if > that max allow
				newvolume = settings.MaxVolumen.toInteger()
	    		state.volume = newvolume
				sendEvent(name: "volume", value: state.volume.toInteger(), isStateChange: true)
				
				strvolume = "00"
				if (state.volume.toInteger()<10) strvolume = "0${state.volume}"
				else strvolume = state.volume.toString ()
	   			parent.sendMsg ("<${state.zone}vo${strvolume}")

				//parent.sendMsg ("<${state.zone}vo${state.volume}")
  		} catch (Exception e) {
        		log.warn "Call to off failed: ${e.message}"
    			}
	}
}
def volumeDown() {
	if (state.volume>0){
		if (logEnable) log.debug "Volumen DOWN ${state.volume}"
    		try {
	    		def newvolume = ((state.volume as long) - (settings.Percent as long))
	    		log.debug newvolume
			if (newvolume <0)
				newvolume = 0
	    		state.volume = newvolume
				sendEvent(name: "volume", value: state.volume, isStateChange: true)
				
				strvolume = "00"
				if (state.volume.toInteger()<10) strvolume = "0${state.volume}"
				else strvolume = state.volume.toString ()
	   			parent.sendMsg ("<${state.zone}vo${strvolume}")				
	    		
				
				//parent.sendMsg ("<${state.zone}vo${state.volume}")
  		} catch (Exception e) {
        		log.warn "Call to off failed: ${e.message}"
    			}
	}
}
def setVolume(Volume) {
    if (logEnable) log.debug Volume
    try {
	    if (Volume>settings.MaxVolumen)
	    	Volume = settings.MaxVolumen
	    if (Volume<0)
	    	Volume = 0
	    state.volume = Volume
	    sendEvent(name: "volume", value: state.volume, isStateChange: true)
		
		strvolume = "00"
		if (state.volume.toInteger()<10) strvolume = "0${state.volume}"
		else strvolume = state.volume.toString ()
	   	parent.sendMsg ("<${state.zone}vo${strvolume}")
	    
		
		
		//parent.sendMsg ("<${state.zone}vo${state.volume}")
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def mute() {
    if (logEnable) log.debug "mute"
    try {
	    	state.mute = "muted"
		sendEvent(name: "mute", value: "muted", isStateChange: true)
	    	parent.sendMsg ("<${state.zone}mu01")
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def unmute() {
    if (logEnable) log.debug "unmute"
    try {
	    	state.mute = "unmuted"
		sendEvent(name: "mute", value: "unmuted", isStateChange: true)
	    	parent.sendMsg ("<${state.zone}mu00")
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
def Source1(){
    if (logEnable) log.debug "Source1"
    try {
		state.mediaSource = 1
		sendEvent(name: "mediaSource", value: 1, isStateChange: true)
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
		parent.sendMsg ("<${state.zone}ch0${state.mediaSource}")
  } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
