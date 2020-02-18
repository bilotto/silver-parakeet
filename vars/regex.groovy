import java.util.regex.Pattern

Boolean stringMatches(String string, String expression){
	def pattern = ~"${expression}"
	if (string =~ pattern) {
		return true
	}
	return false
}


def getMatch(String string, String expression){
	def pattern = ~"${expression}"
	def matcher = string =~ pattern
	if (matcher.find()) {
		return matcher
	}
	return null
}

