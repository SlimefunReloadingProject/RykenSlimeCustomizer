function tick(info) {
    var machine = info.machine();
    var location = info.block().getLocation();
    machine.removeCharge(location, 100);
}

function onPlace(e) {
    var player = e.getPlayer();
    sendMessage(player, "Block placed");
}

function onBreak(e, item, drops) {
    var player = e.getPlayer();
    sendMessage(player, "Block broke");
}