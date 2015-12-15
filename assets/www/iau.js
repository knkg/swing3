function IAUPlugin() { 
} 

IAUPlugin.prototype.compareEngineVersion = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "compareEngineVersion", []); 
};

IAUPlugin.prototype.compareAppVersion = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "compareAppVersion", []); 
};

IAUPlugin.prototype.removeSecurityResource = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "removeSecurityResource", []); 
};

cordova.addConstructor(function() { 
	if (!window.plugins) {
		window.plugins = {};
	}
	if (!window.plugins.iau) {
		window.plugins.iau = new IAUPlugin();
	}
});