def call(Map baseMap, Map otherMap){
	if (!otherMap){
		return baseMap
	}
	if (!baseMap){
		return otherMap
	}
	otherMap.each{ key, value ->
		baseMap[key] = value
	}
	return baseMap
}

