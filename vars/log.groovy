def call(String logMessage){
	println logMessage
}

def call(String logType, String logMessage){
	if (logType == "DEBUG") {
		if (env.DEBUG == "true") {
			println logMessage                  
		}
	}
	if (logType == "ERROR") {
		if (env.ERROR == "true") {
			println logMessage                  
		}
	}
}

def raiseError(String logMessage){
	error logMessage
}

return this