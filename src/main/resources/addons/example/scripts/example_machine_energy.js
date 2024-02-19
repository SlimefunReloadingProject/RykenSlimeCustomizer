function tick(info) {
    var component = info.component();
    component.removeCharge(location, 100);
}

function onPlace(e) {
    var player = e.getPlayer();
    sendMessage(player, "Block placed");
}

function onBreak(e, item, drops) {
    var player = e.getPlayer();
    sendMessage(player, "Block broke");
}