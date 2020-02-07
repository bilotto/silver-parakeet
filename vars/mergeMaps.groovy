def call(Map baseMap, Map otherMap){
	if (!baseMap || !otherMap){
		return baseMap
	}
	otherMap.each{ key, value ->
		baseMap[key] = value
	}
	return baseMap
}