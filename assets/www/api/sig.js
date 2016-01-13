/**
 * cordova Maginsoft plugin
 */
 (function(cordova){
    var Signature = function() {
    };
    Signature.prototype.open = function(success, fail, params) {
        return cordova.exec(function(args) {
            success(args);
        }, function(args) {
            fail(args);
        }, 'Signature', 'open', params);
    };

    window.signature = new Signature();
    
    // backwards compatibility
    window.plugins = window.plugins || {};
    window.plugins.signature = window.signature;
    
})(window.PhoneGap || window.Cordova || window.cordova);