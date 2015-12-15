function InAppUpdatePlugin() { 
} 

InAppUpdatePlugin.prototype.InAppProgress = null;
InAppUpdatePlugin.prototype.updatePolicy = {};
InAppUpdatePlugin.prototype.updatePolicy.refreshUpdatePolicy = null;
InAppUpdatePlugin.prototype.updatePolicy.engineUpdatePolicy = null;
InAppUpdatePlugin.prototype.updatePolicy.applicationUpdatePolicy = null;
InAppUpdatePlugin.prototype.updatePolicy.securityPolicy = null;
InAppUpdatePlugin.prototype.updatePolicy.customResourcePolicy = null;
InAppUpdatePlugin.prototype.firstLaunch = false;
InAppUpdatePlugin.prototype.engineStatus = false;
InAppUpdatePlugin.prototype.applicationStatus = false;
InAppUpdatePlugin.prototype.backupStatus = false;

InAppUpdatePlugin.prototype.init = function(win, fail) { 
	//progressBar 시작 로컬패치 20%;
	window.plugins.inappupdate.setProgressBar(20);
	return cordova.exec(win, fail, "InAppUpdatePlugin", "init", []);
};

InAppUpdatePlugin.prototype.ping = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "ping", []); 
};

InAppUpdatePlugin.prototype.copyResource = function(win, fail) { 
	 return cordova.exec(win, fail, "InAppUpdatePlugin", "copyResource", []); 
};

InAppUpdatePlugin.prototype.checkEngineUpdate = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "checkEngineUpdate", []); 
};

InAppUpdatePlugin.prototype.getEngineUpdate = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "getEngineUpdate", []); 
};

InAppUpdatePlugin.prototype.getEngineRecovery = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "getEngineRecovery", []); 
};

InAppUpdatePlugin.prototype.doBackup = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "doBackup", []); 
};

InAppUpdatePlugin.prototype.doRestore = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "doRestore", []); 
};

InAppUpdatePlugin.prototype.checkAppUpdate = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "checkAppUpdate", []); 
};

InAppUpdatePlugin.prototype.getAppUpdate = function(win, fail) { 
	return cordova.exec(win, fail, "InAppUpdatePlugin", "getAppUpdate", []); 
};

InAppUpdatePlugin.prototype.getAppRecovery = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "getAppRecovery", []); 
};

InAppUpdatePlugin.prototype.getSecurityCheck = function(win, fail) {
	return cordova.exec(win, fail, "InAppUpdatePlugin", "getSecurityCheck", []); 
};

InAppUpdatePlugin.prototype.getRefreshUpdate = function(win, fail) { 
	return cordova.exec(win, fail, "InAppUpdatePlugin", "checkRefreshUpdate", []); 
};

InAppUpdatePlugin.prototype.getCustomResource = function(win, fail) { 
	return cordova.exec(win, fail, "InAppUpdatePlugin", "getCustomResource", []); 
};

InAppUpdatePlugin.prototype.showProgress = function(progressBox) {
    if (this.InAppProgress) {
        this.InAppProgress.spin(document.getElementById(progressBox));
    } else {
        var InAppProgressOpts = {
            lines : 10,
            length : 8,
            width : 4,
            radius : 10,
            color : '#000',
            speed : 1,
            trail : 56,
            shadow : true
        };
        var spinnerBox;
        if (!progressBox) {
            spinnerBox = "progressBox";
        } else {
            spinnerBox = progressBox;
        }
        this.InAppProgress = new Spinner(InAppProgressOpts).spin(document.getElementById(spinnerBox));
    }
};
var __progressValue = 0;
var __limitValue = 0;
var __progressTimer;
InAppUpdatePlugin.prototype.setProgressBar = function(limit){
	if( __progressTimer != null ){ clearTimeout( __progressTimer ); }
	var temp = __limitValue - __progressValue;
	if (__progressValue != 0 && temp > limit - __limitValue) __progressValue = __limitValue;
	__limitValue = limit;
	setProgressBar(1);
}

InAppUpdatePlugin.prototype.hideProgress = function() {
    this.InAppProgress.stop();
};

InAppUpdatePlugin.prototype.launch = function() {
	var txt = document.getElementById("szazalek_txt").innerHTML;
	if (txt.indexOf("100") < 0) {
		setTimeout("window.plugins.inappupdate.launch();", 5);
		return;
	}
    return cordova.exec(null, null, "InAppUpdatePlugin","launch",[]);
};

InAppUpdatePlugin.prototype.getRefreshUpdatePolicy = function() {
	//progressBar 개별파일 업데이트 20%;
	window.plugins.inappupdate.setProgressBar(95);
    return this.updatePolicy.refreshUpdatePolicy;
};

InAppUpdatePlugin.prototype.getApplicationUpdatePolicy = function() {
	//progressBar 업무파일 업데이트 30%;
	window.plugins.inappupdate.setProgressBar(75);
    return this.updatePolicy.applicationUpdatePolicy;
};

InAppUpdatePlugin.prototype.getEngineUpdatePolicy = function() {
	//progressBar 엔진 업데이트 25%;
	window.plugins.inappupdate.setProgressBar(45);
	return this.updatePolicy.engineUpdatePolicy;    
};
InAppUpdatePlugin.prototype.setProgressEnd = function() {
	if( __progressTimer != null ){ clearTimeout( __progressTimer ); }
	__progressValue = 100;
	window.plugins.inappupdate.setProgressBar(100);
}
InAppUpdatePlugin.prototype.getSecurityPolicy = function() {
	return this.updatePolicy.securityPolicy;    
};

InAppUpdatePlugin.prototype.customResourcePolicy = function() {
	//progressBar custom 업데이트 5%;
	window.plugins.inappupdate.setProgressBar(100);
	return this.updatePolicy.customResourcePolicy;    
};

InAppUpdatePlugin.prototype.isFirstLaunch = function() {
    return this.firstLaunch;    
};

InAppUpdatePlugin.prototype.clean = function() {
    return cordova.exec(null, null, "InAppUpdatePlugin", "clean",[]);
};

InAppUpdatePlugin.prototype.setStatusFunction = function(name) {
	return cordova.exec(null, null, "InAppUpdatePlugin", "setStatusFunction", [name]);
};

InAppUpdatePlugin.prototype.setProgressFunction = function(name) {
	return cordova.exec(null, null, "InAppUpdatePlugin", "setProgressFunction", [name]);
};

var debug_mode = 0;
var error_mode = 0;

document.addEventListener("deviceready",onDeviceReady,false);

function onDeviceReady() {
	noticeCheck();
}

function onInitSuccess() {
	window.plugins.inappupdate.copyResource(checkBackup, onFail);
}	

function checkBackup() {
	console.log("Backup / recovery process");
	if (!window.plugins.inappupdate.applicationStatus || !window.plugins.inappupdate.engineStatus) {
		if (window.plugins.inappupdate.backupStatus) {
			window.plugins.inappupdate.doRestore(checkEngineUpdate, onFail);
		} else {
			checkEngineUpdate();
		}
	} else {
		checkEngineUpdate();
	}
}

function checkApplicationUpdate() {
	console.log("checkApplicationUpdate");
	if (window.plugins.inappupdate.getApplicationUpdatePolicy()) {
		onApplicationUpdate();
	} else {
		checkRefreshUpdate();
	}
}

function checkRefreshUpdate() {
	console.log("checkRefreshUpdate");
	if (window.plugins.inappupdate.getRefreshUpdatePolicy()) {
		onRefreshUpdate();
	} else {
		checkCustomResource();
	}
}

function checkCustomResource() {
	console.log("checkCustomResource");
	if (window.plugins.inappupdate.customResourcePolicy()) {
		onCustomResource();
	} else {
		onProgressEnd();
	}
}

function launch() {
	//window.plugins.inappupdate.hideProgress();
	var progressdiv = document.getElementById("progressMessage");
	progressdiv.innerHTML= "";
	window.plugins.inappupdate.launch();
}

function onLaunch(url) {
	location.replace(url);
}

function onSecurityCheck() {
	console.log("onSecurityCheck");
	try {
		if (window.plugins.inappupdate.getSecurityPolicy()) {
			window.plugins.inappupdate.getSecurityCheck(launch, onFail);
		} else {
			onProgressEnd();
		}
	}catch(e) {
		alert(e.message);
	}
}
function onProgressEnd() {
	window.plugins.inappupdate.setProgressEnd();
	launch();
}

function onCustomResource() {
	console.log("onCustomResource");
	window.plugins.inappupdate.getCustomResource(onProgressEnd, onFail);
}

function onRefreshUpdate() {
	console.log("onRefreshUpdate");
	window.plugins.inappupdate.getRefreshUpdate(checkCustomResource, onFail);
}

function onEngineUpdate() {
	console.log("onEngineUpdate");
	window.plugins.inappupdate.getEngineUpdate(checkApplicationUpdate, onFail);
}

function onEngineRecovery() {
	console.log("onEngineRecovery");
	window.plugins.inappupdate.getEngineRecovery(checkApplicationUpdate, onFail);
}        

function onApplicationUpdate() {
	console.log("onApplicationUpdate");
	window.plugins.inappupdate.getAppUpdate(checkRefreshUpdate, onFail);
}

function onApplicationRecovery() {
	console.log("onApplicationRecovery");
	window.plugins.inappupdate.getAppRecovery(checkRefreshUpdate, onFail);
}

function checkEngineUpdate() {
	if(window.plugins.inappupdate.getEngineUpdatePolicy()) {
		if(!window.plugins.inappupdate.engineStatus) {
			onEngineRecovery();
		} else {
			onEngineUpdate();
		}			
	} else {
		checkApplicationUpdate();
	}
}

function onFail(errormsg) {
	setStatus(errormsg);
	if( __progressTimer != null ){ clearTimeout( __progressTimer ); }
//	window.plugins.inappupdate.hideProgress();
    
//	error_switch();
	error_on(); // 수동으로 설정
}

function setStatus(msg) {
//	alert(msg);
	var logdiv = document.getElementById("progressStatus");
	logdiv.innerHTML = msg;
}

function setProgress(progressInfo) {
    var progressdiv = document.getElementById("progressMessage");
    var filename = progressInfo.name;
    if (filename.length > 15) {
        filename = filename.substr(0,12) + "...";
    }
    if (progressInfo.progress >= 0.999) {
        progressdiv.innerHTML = "";
    } else {
    	if (progressInfo.progress > 0 ) {
            var progressdata = Math.round(progressInfo.progress * 100);
            var progressmessage = progressInfo.message;
            progressdiv.innerHTML = filename + " " + progressdata  + "% " + progressmessage;
    	} else {
    		progressdiv.innerHTML = filename + " " + progressInfo.message;
    	}
    }
}
function setProgressBar(i){
	var progress = 0;
	if (i > 0) {
		progress = (1.0/i) - (1.0/(i+1));
		if (i == 1) progress = progress/4;
		__progressValue = __progressValue + (__limitValue - __progressValue) * progress;
		progress = Math.floor(__progressValue);
	}
	document.getElementById("szliderbar").style.width=progress+'%';
	document.getElementById("szazalek_txt").innerHTML=progress+'%';
	if (__progressValue > 0 && __progressValue < 100) {
		__progressTimer = setTimeout("setProgressBar(2);",  500);
	} else {
		if( __progressTimer != null ){ clearTimeout( __progressTimer ); }
	}
}

function debug_switch() {
	if(debug_mode == 0) {
		debug_mode = 1;
		document.getElementById("errorfunctions").style.display = "block";                
	} else {
		debug_mode = 0;
		document.getElementById("errorfunctions").style.display = "none"; 
	}
}

function error_on() {
    error_mode = 1;
    document.getElementById("debugarea").style.display="block";
}

function noticeCheck(){
	cordova.exec(null, null, "InAppUpdatePlugin", "noticeCheck", []);
}

function startInit() {
	__progressValue = 0;
	setProgressBar(0);
	document.getElementById("inappframe").style.display = "block";
//	window.plugins.inappupdate.showProgress("progressBox");
	window.plugins.inappupdate.setStatusFunction("setStatus");
	window.plugins.inappupdate.setProgressFunction("setProgress");
	window.plugins.inappupdate.init(onInitSuccess, onFail);
}

function onNotice(noticeInfo){
	window.plugins.inappupdate.noticeInfo = noticeInfo;
	window.plugins.inappupdate.noticeIdx = 0;
	document.getElementById("noticeframe").style.display = "block";
	nextNotice();
}

function nextNotice(){
	var noticeInfo = window.plugins.inappupdate.noticeInfo;
	var noticeIdx = window.plugins.inappupdate.noticeIdx;
	if (noticeIdx >= noticeInfo.length) {
			if (noticeInfo[noticeIdx - 1]["sng"] == "go") {
				document.getElementById("noticeframe").style.display = "none";
				startInit();
				return;
			}
	} else {
		if (noticeInfo[noticeIdx]["sng"] == "stop") {
			document.getElementById("noticeNext").style.display = "none";
		} else {
			document.getElementById("noticeNext").style.display = "block";
		}
		var iframe = document.getElementById("noticeIframe");
		var textarea = document.getElementById("cont");
		if (noticeInfo[noticeIdx]["url"] && noticeInfo[noticeIdx]["url"].length > 0){
			iframe.src = noticeInfo[noticeIdx]["url"];
			iframe.style.display = "block";
			textarea.style.display = "none";
		} else if (noticeInfo[noticeIdx]["comment"] && noticeInfo[noticeIdx]["comment"].length > 0){
			document.getElementById("noticeTitle").innerHTML = decode(noticeInfo[noticeIdx]["title"]);
			document.getElementById("noticeContent").innerHTML = decode(noticeInfo[noticeIdx]["comment"]);
			iframe.style.display = "none";
			textarea.style.display = "block";
		}
		window.plugins.inappupdate.noticeIdx = noticeIdx + 1;
	}
}

function getMacAddress() {
	window.plugins.misc.getMacAddress(setStatus,onFail);
}

cordova.addConstructor(function() {
	if (!window.plugins) {
		window.plugins = {};
	}
	if (!window.plugins.inappupdate) {
		window.plugins.inappupdate = new InAppUpdatePlugin();
	}
});
function decode(s) {
	var re1 = /&lt;/g;	//Initialize pattern.
	var re2 = /&gt;/g;	//Initialize pattern.
	var re3 = /&apos;/g;	//Initialize pattern.
	var re4 = /&quot;/g;	//Initialize pattern.
	var re5 = /&amp;/g;	//Initialize pattern.
	var re6 = /&#xA;/g;	//Initialize pattern.
	return s.replace( re1 ,"<").replace( re2 ,">").replace( re3 ,"'").replace( re4 ,"\"").replace( re5 ,"&").replace( re6 ,"\n");
}