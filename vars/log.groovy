def call(String logMessage){
	println logMessage
}

def call(String logType, String logMessage){
	if (!env.logType) {
		return false
	}
	if (env.logType == 'true'){
		println logMessage
	}
	return true
}

def raiseError(String logMessage){
	error logMessage
}

return this