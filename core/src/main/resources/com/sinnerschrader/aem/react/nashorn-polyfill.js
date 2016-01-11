var global = this;
global.setTimeout = function() {
	console.log("setTimeout is not implemented");
}
global.setInterval = function() {
	console.log("setInterval is not implemented");
}
global.clearInterval = function() {
	console.log("clearInterval is not implemented");
}
// initialize AemGlobal here instead of in server.tsx, which does not reliably work.
AemGlobal = {};
