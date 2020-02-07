def call(Map baseMap, key, value){
	new_map = [:]
	new_map.put(key, value)
	return mergeMaps(baseMap, new_map)
}