function checkBlock(object) {
    let id = object.id.split('@')[0];
    document.getElementById(id + '@block_value').disabled = false;
}