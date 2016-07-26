/* We don't need any IPC with Java, but we have to ship this because it's somehow required by jxcore....
 */
var events = require('events');
var javaEmitter = new events.EventEmitter();

function JSFunction(name, jsFunction){
    this.name = name;
    this.jsFunction = jsFunction;
}

var JXCoreModule = {
    callJavaFunction: null,
    init: function(callJavaFunction){
        this.callJavaFunction = callJavaFunction;
        console.log('JXCoreModule Init');
        setTimeout(function(){
            JXCoreModule.callJavaFunction('onInitialized');
        }, 100);
    },
    registerJSFunction: function(){
        var self = this;
        // it returns all registered Java functions; for this example it returns [onInitialized, out]
        var javaFunctions = new JSFunction('javaFunctions', function() {
            javaEmitter.emit('javaFunctions', Array.prototype.slice.call(arguments));
        } );

        //add your java function here like this one
        var inFunc = new JSFunction('in', function(functionName, data) {
            //EventEmitter just for example, it simplifies the interaction with native code
            javaEmitter.emit('in', functionName, data);
        } );
        return [javaFunctions, inFunc];
    }
};

javaEmitter.on('out', function(functionName, data) {
  // just call function by name if you are sure that it exists
  JXCoreModule.callJavaFunction('out', functionName, data);
});

module.exports.JXCoreModule = JXCoreModule;  // it need for the system, do not remove this export
module.exports.JavaEmitter = javaEmitter;    // do what you want with this export

// End of JXCore stuff

console.log("-> Initializing Stremio server");
require("app/jxcore-start.js");