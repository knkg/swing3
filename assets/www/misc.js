function importJS (filename){
	var head= document.getElementsByTagName('head')[0];
	var script= document.createElement('script');
	script.type= 'text/javascript';
	script.src= filename;
	head.appendChild(script);
}

function MiscPlugin() { 
} 

MiscPlugin.prototype.photoMMS = function(win, fail, img, number, msg) {
	return cordova.exec(win, fail, "Misc", "photoMMS", [img, number, msg]); 
};

MiscPlugin.prototype.getMacAddress = function(win, fail) {
	return cordova.exec(win, fail, "Misc", "getMacAddress", []); 
};

MiscPlugin.prototype.getAppId = function(win, fail) {
	return cordova.exec(win, fail, "Misc", "getAppId", []); 
};

MiscPlugin.prototype.getVersion = function(win, fail) {
	return cordova.exec(win, fail, "Misc", "getVersion", []); 
};

MiscPlugin.prototype.exit = function(fail) {
	return cordova.exec(null, fail, "Misc", "exit", []); 
};

MiscPlugin.prototype.callApp = function(win, fail, packageName, arg) {
	return cordova.exec(win, fail, "Misc", "callApp", [packageName, arg]); 
};

MiscPlugin.prototype.getAppData = function(win, fail, contentName) {
	return cordova.exec(win, fail, "Misc", "getAppData", [contentName]); 
};

MiscPlugin.prototype.setLanguage = function(win, fail, lang) {
	return cordova.exec(win, fail, "Misc", "setLanguage", [lang]); 
};

MiscPlugin.prototype.getLanguage = function(win, fail) {
	return cordova.exec(win, fail, "Misc", "getLanguage", []); 
};

MiscPlugin.prototype.getDocument = function(win, fail, mimetype, url, data) {
	return cordova.exec(win, fail, "Misc", "getDocument", [mimetype, url, data]);
};

cordova.addConstructor(function() { 
	if (!window.plugins) {
		window.plugins = {};
	}
	if (!window.plugins.misc) {
		window.plugins.misc = new MiscPlugin();
	}
});