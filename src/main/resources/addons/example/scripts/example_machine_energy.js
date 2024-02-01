function tick(info) {
    addClickHandler(0, theClick);
}

var theClick = function(p,s,is,click){
    p.sendMessage("HELLO");
}