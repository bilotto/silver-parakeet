import java.util.regex.Pattern

def stringMatches(String string, String expression){
	def pattern = ~"${expression}"
	def matcher = string =~ pattern
	if (matcher.find()) {
		return matcher
	}
	return null
}

