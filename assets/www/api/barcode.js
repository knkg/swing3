/**
 * cordova Maginsoft FileChooser plugin
 */
 (function(cordova){
    var MFileChooser = function() {
    };
    MFileChooser.prototype.open = function(success, fail, params) {
        return cordova.exec(function(args) {
            success(args);
        }, function(args) {
            fail(args);
        }, 'MFileChooser', 'open', params);
    };
    
    var BarcodeScanner = function() {
    };
    BarcodeScanner.prototype.scan = function(success, fail, params) {
        return cordova.exec(function(args) {
            success(args);
        }, function(args) {
            fail(args);
        }, 'BarcodeScanner', 'scan', params);
    };

    window.mfilechooser = new MFileChooser();
    window.barcodescanner = new BarcodeScanner();
    
    // backwards compatibility
    window.plugins = window.plugins || {};
    window.plugins.mfilechooser = window.mfilechooser;
    window.plugins.barcodescanner = window.barcodescanner;
    
})(window.PhoneGap || window.Cordova || window.cordova);