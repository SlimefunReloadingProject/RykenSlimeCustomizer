function tick(info) {
    if (working) {
        setWorking(false);
    }
}

function onPlace(e) {
    var player = e.getPlayer();
    sendMessage(player, "Block placed");
}

function onBreak(e, item, drops) {
    var player = e.getPlayer();
    sendMessage(player, "Block broke");
}