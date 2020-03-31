def call(logMessage){
	println logMessage
}

def call(String logType, String logMessage){
	logMessage = "${logType}: ${logMessage}"
	if (logType == "DEBUG") {
		if (env.DEBUG) {
			if (env.DEBUG == "true") {
				println logMessage                  
			}    
		}
	}
	if (logType == "INFO") {
		if (env.INFO) {
			if (env.INFO == "true") {
				println logMessage                  
			}    
		}
	}
	if (logType == "ERROR") {
		if (env.ERROR) {
			if (env.ERROR == "true") {
				println logMessage                  
			}    
		}
	}
}

def raiseError(String logMessage){
	this.call("ERROR", logMessage)
	error logMessage
}

